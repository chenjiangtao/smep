package com.aesirteam.smep.adc.services;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.aesirteam.smep.adc.db.domain.SmepAdcCorpInfo;
import com.aesirteam.smep.adc.db.domain.SmepAdcStaffInfo;
import com.aesirteam.smep.adc.db.domain.SmepAdcStaffParam;
import com.aesirteam.smep.adc.db.mapper.SmepAdcCorpInfoMapper;
import com.aesirteam.smep.adc.db.mapper.SmepAdcStaffInfoMapper;
import com.aesirteam.smep.adc.db.mapper.SmepAdcStaffParamMapper;
import com.aesirteam.smep.adc.message.MsgHeader;
import com.aesirteam.smep.adc.message.StaffBindReq;
import com.aesirteam.smep.adc.message.StaffBindRsp;
import com.aesirteam.smep.adc.message.StaffInfo;
import com.aesirteam.smep.adc.message.StaffInfoResult;

@Service
public class StaffBindService {
	
	@Resource(name="smepAdcCorpInfoMapper")
	SmepAdcCorpInfoMapper corpInfoMapper;
	
	@Resource(name="smepAdcStaffInfoMapper")
	SmepAdcStaffInfoMapper staffInfoMapper;
	
	@Resource(name="smepAdcStaffParamMapper")
	SmepAdcStaffParamMapper staffParamMapper;
	
	private String xmlText;
	
	private boolean isChinaMobileNumber(String mobile) {
		final String cm_regex = "^(([+]?86))?(((134[0-8])\\d{7}))|(((13[5-9])|147|(15[0-2,7-9])|(18[2-4,7-8]))\\d{8})$";
		return Pattern.compile(cm_regex).matcher(mobile).find();
	}
	
	public StaffBindRsp execute() throws Exception {
		int resultcode = -1;
		StaffBindReq staffBindReq = new StaffBindReq(xmlText);
		List<StaffInfoResult> result = new ArrayList<StaffInfoResult>();
		String corpAccount = staffBindReq.getCorpAccount();
		MsgHeader header = staffBindReq.getHeader();
		
		if (null == header.getServiceId() || header.getServiceId().length() == 0) {
			return new StaffBindRsp(header, corpAccount, AdcConstant.HEADER_SERVICEID_ISNULL);
		}
		
		if (null == corpAccount || corpAccount.length() == 0) {
			return new StaffBindRsp(header, corpAccount, AdcConstant.CORP_ACCOUNT_ISNULL);
		}
		
		//检查企业订购状态
		SmepAdcCorpInfo smepAdcCorpInfo = corpInfoMapper.getData(corpAccount);
		//System.out.println(" smepAdcCorpInfo:" + smepAdcCorpInfo.getOptype());
		//企业处于退订状态时，全部操作失效
		if (null == smepAdcCorpInfo || 5 == smepAdcCorpInfo.getOptype()) {
			return new StaffBindRsp(header, corpAccount, AdcConstant.CORP_ORDER_NOTFOUND);
		} 
				
		for(StaffInfo info : staffBindReq.getStaffList()) {
			String ufId = info.getUfId();
			
			if (null == ufId || ufId.length() == 0) {
				result.add(this.createStaffInfoResult(info, AdcConstant.STAFF_UFID_ISNULL));
				continue;
			}
			
			if (null == info.getUserType() || info.getUserType().length() == 0) {
				result.add(this.createStaffInfoResult(info, AdcConstant.STAFF_USERTYPE_ISNULL));
				continue;
			}
			
			if (!"0,1,2,3".contains(info.getUserType())) {
				result.add(this.createStaffInfoResult(info, AdcConstant.STAFF_USERTYPE_FAIL));
				continue;
			}
			
			
			if (null == info.getOptype() || info.getOptype().length() == 0) {
				result.add(this.createStaffInfoResult(info, AdcConstant.STAFF_OPTYPE_ISNULL));
				continue;
			}
			
			if (!"1,2,3,4,5".contains(info.getOptype())) {
				result.add(this.createStaffInfoResult(info, AdcConstant.STAFF_OPTYPE_FAIL));
				continue;
			}
			
			if (info.getStaffMobile().length() > 0 && !this.isChinaMobileNumber(info.getStaffMobile())) {
				result.add(this.createStaffInfoResult(info, AdcConstant.STAFF_MOBILE_FAIL));
				continue;
			}
			
			SmepAdcStaffInfo smepAdcStaffInfo = staffInfoMapper.getData(ufId);
			if (null == smepAdcStaffInfo) {
				//新记录   1-订购
				if ("1".equals(info.getOptype())) {

					smepAdcStaffInfo = info.convertStaffInfo(corpAccount);
					List<SmepAdcStaffParam> list = info.convertStaffParam(ufId);
					
					if (0 == list.size()) {
						result.add(this.createStaffInfoResult(info, AdcConstant.STAFF_PARAM_FAIL));
						continue;
					}
					
					resultcode = staffInfoMapper.insertData(smepAdcStaffInfo) == 1 && 0 < staffParamMapper.insertData(list) 
							? AdcConstant.STAFF_ORDER_SUCCESS : AdcConstant.STAFF_ORDER_FAIL;
					result.add(this.createStaffInfoResult(info, resultcode));
				} else {
					result.add(this.createStaffInfoResult(info, AdcConstant.STAFF_ORDER_NOTFOUND));
				}
			} else {
				
				//不允许重复订购
				if (5 != smepAdcStaffInfo.getOptype() && "1".equals(info.getOptype())) {
					result.add(this.createStaffInfoResult(info, AdcConstant.STAFF_ORDER_EXISTS));
					continue;
				}
				
				// 当退订状态时，只能重新订购
				if (5 == smepAdcStaffInfo.getOptype() && !"1".equals(info.getOptype())) {
					result.add(this.createStaffInfoResult(info, AdcConstant.STAFF_STATUS_UNORDER));
					continue;
				}
				
				if ("1".equals(info.getOptype()) || "4".equals(info.getOptype())) {
					//这里不删除旧的参数，只做更新会有一部分冗余但不影响业务
					smepAdcStaffInfo = info.convertStaffInfo(corpAccount);
					List<SmepAdcStaffParam> list = info.convertStaffParam(ufId);
					
					if (0 == list.size()) {
						result.add(this.createStaffInfoResult(info, AdcConstant.STAFF_PARAM_FAIL));
						continue;
					}
					
					boolean eflag = staffInfoMapper.updateData(smepAdcStaffInfo) == 1 && 0 < this.batchUpdateSmepAdcStaffParamMapper(list);
					
					if ("1".equals(info.getOptype())) {
						resultcode = eflag ? AdcConstant.STAFF_ORDER_SUCCESS : AdcConstant.STAFF_ORDER_FAIL;
					} else {
						resultcode = eflag ? AdcConstant.STAFF_INFO_CHANGE_SUCCESS : resultcode;
					}
					
					result.add(this.createStaffInfoResult(info, resultcode));
					continue;
				}
				
				if ("2".equals(info.getOptype()) || "3".equals(info.getOptype()) || "5".equals(info.getOptype())) {
					smepAdcStaffInfo = new SmepAdcStaffInfo();
					smepAdcStaffInfo.setUfid(ufId);
					smepAdcStaffInfo.setOptype(Integer.valueOf(info.getOptype()));
					smepAdcStaffInfo.setOpnote(info.getOpnote());
	
					boolean eflag = staffInfoMapper.updateData(smepAdcStaffInfo) == 1; 
	
					if ("5".equals(info.getOptype())) {
						resultcode = eflag ? AdcConstant.STAFF_UNORDER_SUCCESS :resultcode;
						
					} else {
						resultcode = eflag ? AdcConstant.STAFF_STATUS_CHANGE_SUCCESS :resultcode;
					}
					result.add(this.createStaffInfoResult(info, resultcode));
				}			
			}
		}
		
		return new StaffBindRsp(header, corpAccount, result);
	}
	
	private int batchUpdateSmepAdcStaffParamMapper(List<SmepAdcStaffParam> list) {
		int count = 0;
		for(SmepAdcStaffParam smepAdcCorpParam : list) {
			if (1 == staffParamMapper.updateData(smepAdcCorpParam)) count++; 
		}
		return count;
	}
	
	private StaffInfoResult createStaffInfoResult(StaffInfo info, int resultcode) {
		//组装应答消息
		StaffInfoResult staffInfoResult = new StaffInfoResult();
		staffInfoResult.setUfId(info.getUfId());
		staffInfoResult.setStaffName(info.getStaffName());
		staffInfoResult.setStaffMobile(info.getStaffMobile());
		//根据业务检查结果进行赋值
		switch(resultcode) {
			case  AdcConstant.STAFF_ORDER_SUCCESS:
				staffInfoResult.setResultcode("0");
				staffInfoResult.setResultmsg("用户订购产品成功");
				break;
				
			case AdcConstant.STAFF_ORDER_FAIL:
				staffInfoResult.setResultcode("1");
				staffInfoResult.setResultmsg("用户订购产品失败");
				break;
				
			case AdcConstant.STAFF_ORDER_NOTFOUND:
				staffInfoResult.setResultcode("1");
				staffInfoResult.setResultmsg("SI系统未发现用户的订购关系");
				break;
			
			case AdcConstant.STAFF_ORDER_EXISTS:
				staffInfoResult.setResultcode("1");
				staffInfoResult.setResultmsg("SI系统已存在该用户订购关系,无需重复此操作");
				break;
				
			case AdcConstant.STAFF_PARAM_FAIL:
				staffInfoResult.setResultcode("1");
				staffInfoResult.setResultmsg("用户参数信息错误");
				break;
				
			case AdcConstant.STAFF_STATUS_UNORDER:
				staffInfoResult.setResultcode("1");
				staffInfoResult.setResultmsg("用户为退订状态，无法完成此操作");
				break;
				
			case AdcConstant.CORP_UNORDER_SUCCESS:
				staffInfoResult.setResultcode("0");
				staffInfoResult.setResultmsg("用户退订产品成功");
				break;
				
			case AdcConstant.STAFF_STATUS_CHANGE_SUCCESS:
				staffInfoResult.setResultcode("0");
				staffInfoResult.setResultmsg("用户订购状态变更成功");
				break;
				
			case AdcConstant.STAFF_INFO_CHANGE_SUCCESS:
				staffInfoResult.setResultcode("0");
				staffInfoResult.setResultmsg("用户信息变更成功");
				break;
				
			case AdcConstant.STAFF_UFID_ISNULL:
				staffInfoResult.setResultcode("1");
				staffInfoResult.setResultmsg("用户编号UFID不能为空");
				break;
				
			case AdcConstant.STAFF_USERTYPE_ISNULL:
				staffInfoResult.setResultcode("1");
				staffInfoResult.setResultmsg("用户类型USERTYPE不能为空");
				break;
				
			case AdcConstant.STAFF_OPTYPE_ISNULL:
				staffInfoResult.setResultcode("1");
				staffInfoResult.setResultmsg("操作类型OPTYPE不能为空");
				break;
			
			case AdcConstant.STAFF_OPTYPE_FAIL:
				staffInfoResult.setResultcode("1");
				staffInfoResult.setResultmsg("未定义的操作类型OPTYPE");
				break;
				
			case AdcConstant.STAFF_MOBILE_FAIL:
				staffInfoResult.setResultcode("502");
				staffInfoResult.setResultmsg("用户手机号码非法亦或不是中国移动用户");
				break;
			
			case AdcConstant.STAFF_USERTYPE_FAIL:
				staffInfoResult.setResultcode("1");
				staffInfoResult.setResultmsg("未定义的用户类型USERTYPE");
				break;
				
			default:
				staffInfoResult.setResultcode("1");
				staffInfoResult.setResultmsg("其他错误");
				break;
		}
		
		return staffInfoResult;
	}
	
	public String getXmlText() {
		return xmlText;
	}

	public void setXmlText(String xmlText) {
		this.xmlText = xmlText;
	}
}
