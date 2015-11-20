package com.aesirteam.smep.adc.services;

import java.util.List;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.aesirteam.smep.adc.db.domain.SmepAdcCorpInfo;
import com.aesirteam.smep.adc.db.domain.SmepAdcCorpParam;
import com.aesirteam.smep.adc.db.domain.SmepAdcStaffInfo;
import com.aesirteam.smep.adc.db.mapper.SmepAdcCorpInfoMapper;
import com.aesirteam.smep.adc.db.mapper.SmepAdcCorpParamMapper;
import com.aesirteam.smep.adc.db.mapper.SmepAdcStaffInfoMapper;
import com.aesirteam.smep.adc.message.CorpBindReq;
import com.aesirteam.smep.adc.message.MsgHeader;

@Service
public class CorpBindService extends BaseService {
	
	@Resource(name="smepAdcCorpInfoMapper")
	SmepAdcCorpInfoMapper corpInfoMapper;

	@Resource(name="smepAdcCorpParamMapper")
	SmepAdcCorpParamMapper corpParamMapper;
	
	@Resource(name="smepAdcStaffInfoMapper")
	SmepAdcStaffInfoMapper staffInfoMapper;
	
	private String xmlText;
	private MsgHeader header;
	
	public int execute() throws Exception {
		int resultcode = 404;
		CorpBindReq corpBindReq = new CorpBindReq(xmlText);
		this.header = corpBindReq.getHeader();
		
		if (null == header.getServiceId() || header.getServiceId().length() == 0) {
			return AdcConstant.HEADER_SERVICEID_ISNULL;
		}
		
		if (null == corpBindReq.getCorpAccount() || corpBindReq.getCorpAccount().length() == 0) {
			return AdcConstant.CORP_ACCOUNT_ISNULL;
		}
		
		if (null == corpBindReq.getCorpName() || corpBindReq.getCorpName().length() == 0) {
			return AdcConstant.CORP_NAME_ISNULL;
		}
		
		if (null == corpBindReq.getOptype() || corpBindReq.getOptype().length() == 0) {
			return AdcConstant.CORP_OPTYPE_ISNULL;
		}
		
		if (!"1,2,3,4,5,6".contains(corpBindReq.getOptype())) {
			return AdcConstant.CORP_OPTYPE_FAIL;
		}
		
		SmepAdcCorpInfo smepAdcCorpInfo = corpInfoMapper.getData(corpBindReq.getCorpAccount());
		if (null == smepAdcCorpInfo) {
			//新记录   1-订购
			if ("1".equals(corpBindReq.getOptype())) {
				
				if (null == corpBindReq.getPoint() || corpBindReq.getPoint().length() == 0) {
					return AdcConstant.CORP_POINT_ISNULL;
				}
				
				smepAdcCorpInfo = corpBindReq.convertCorpInfo();
				List<SmepAdcCorpParam> list = corpBindReq.convertCorpParam();
				
				if (0 == list.size()) return AdcConstant.CORP_PARAM_FAIL;
				
				resultcode = corpInfoMapper.insertData(smepAdcCorpInfo) == 1 && 0 < corpParamMapper.insertData(list) 
						? AdcConstant.CORP_ORDER_SUCCESS : AdcConstant.CORP_ORDER_FAIL;		
				return resultcode;
				
			} else {
				return AdcConstant.CORP_ORDER_NOTFOUND;
			}
		} else {
			//不允许重复订购
			if (5 != smepAdcCorpInfo.getOptype() && "1".equals(corpBindReq.getOptype())) {
				return AdcConstant.CORP_ORDER_EXISTS;
			}
			
			// 当退订状态时，只能重新订购
			if (5 == smepAdcCorpInfo.getOptype() && !"1".equals(corpBindReq.getOptype())) {
				return AdcConstant.CORP_STATUS_UNORDER;
			}
			
			if ("1".equals(corpBindReq.getOptype())) {
				//这里不删除旧的参数，只做更新会有一部分冗余但不影响业务
				if (null == corpBindReq.getPoint() || corpBindReq.getPoint().length() == 0) {
					return AdcConstant.CORP_POINT_ISNULL;
				}
				
				smepAdcCorpInfo = corpBindReq.convertCorpInfo();
				List<SmepAdcCorpParam> list = corpBindReq.convertCorpParam();
				if (0 == list.size()) return AdcConstant.CORP_PARAM_FAIL;
				
				resultcode = corpInfoMapper.updateData(smepAdcCorpInfo) == 1 && 0 < this.batchUpdateSmepAdcCorpParamMapper(list)
						? AdcConstant.CORP_ORDER_SUCCESS : AdcConstant.CORP_ORDER_FAIL;
				return resultcode;
			}
			
			if ("2".equals(corpBindReq.getOptype()) || "3".equals(corpBindReq.getOptype()) || "5".equals(corpBindReq.getOptype())) {
				smepAdcCorpInfo = new SmepAdcCorpInfo();
				smepAdcCorpInfo.setCorpaccount(corpBindReq.getCorpAccount());
				smepAdcCorpInfo.setOptype(Integer.valueOf(corpBindReq.getOptype()));
				smepAdcCorpInfo.setOpnote(corpBindReq.getOpnote());
				resultcode = corpInfoMapper.updateData(smepAdcCorpInfo) == 1 ? AdcConstant.CORP_STATUS_CHANGE_SUCCESS : resultcode;
				
				if (!"5".equals(corpBindReq.getOptype())) return resultcode;
				
				//退订所有用户 ===========================
				List<SmepAdcStaffInfo> smepAdcStaffInfoList = staffInfoMapper.getDataByCorpAccount(corpBindReq.getCorpAccount()); 
				int count = 0;
				for (SmepAdcStaffInfo smepAdcStaffInfo : smepAdcStaffInfoList) {
					smepAdcStaffInfo.setOptype(5);
					smepAdcStaffInfo.setOpnote("EC退订导致自动退订");
					count += staffInfoMapper.updateData(smepAdcStaffInfo);
				}
								
				resultcode = count >= 0 ? AdcConstant.CORP_UNORDER_SUCCESS : resultcode;
				return  resultcode;
			}
			
			if ("4".equals(corpBindReq.getOptype()) || "6".equals(corpBindReq.getOptype())) {
				smepAdcCorpInfo = corpBindReq.convertCorpInfo();

				//变更基本信息
				resultcode = corpInfoMapper.updateData(smepAdcCorpInfo) == 1 ? AdcConstant.CORP_INFO_CHANGE_SUCCESS : resultcode;
				if ("6".equals(corpBindReq.getOptype())) return resultcode;
				
				//变更企业参数信息
				List<SmepAdcCorpParam> list = corpBindReq.convertCorpParam();
				
				if (0 == list.size()) return AdcConstant.CORP_PARAM_FAIL;
				
				resultcode = 0 < this.batchUpdateSmepAdcCorpParamMapper(list) ? AdcConstant.CORP_INFO_CHANGE_SUCCESS : resultcode;
			}	
		}
				
		return resultcode;
	}
	
	private int batchUpdateSmepAdcCorpParamMapper(List<SmepAdcCorpParam> list) {
		int count = 0;
		for(SmepAdcCorpParam smepAdcCorpParam : list) {
			if (1 == corpParamMapper.updateData(smepAdcCorpParam)) count++; 
		}
		return count;
	}
	
	public void setXmlText(String xmlText) {
		this.xmlText = xmlText;
	}

	public MsgHeader getHeader() {
		return header;
	}	
}
