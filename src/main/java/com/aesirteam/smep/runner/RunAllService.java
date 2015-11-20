package com.aesirteam.smep.runner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
/*
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
*/
import com.aesirteam.smep.core.ServiceStore;
import com.aesirteam.smep.core.db.domain.SmepSysService;
import com.aesirteam.smep.sms.services.CMPP20MtService;

@SuppressWarnings("rawtypes")
public class RunAllService {
		
	protected Map<String, ServiceStore> serviceMap = new HashMap<String, ServiceStore>();
	
	public RunAllService() throws NullPointerException {
		
		List<SmepSysService> list = ServiceStore.getServiceNames();
		
		for(SmepSysService record : list) {
			ServiceStore service = ServiceStore.get(record.getClassname());
			if (null == service)
				throw new NullPointerException(record.getClassname());

			service.setServiceName(record.getServicename());
			service.setRelationEngine(record.getRelationengine());
			service.setEngineDesc(record.getDesc());
			service.setLastStartTime(record.getLaststarttime());
			service.setLastStopTime(record.getLaststoptime());
			service.setCurrState(record.getCurrstate());
			service.setAutostart(1 == record.getAutostart() ? true : false);
			serviceMap.put(record.getServicename(), service);
		}
	}
	
	public void showService() {
		System.out.println(String.format("%-60s\t%-60s\t%-60s\t%-30s\t%s", "服务名", "状态","关联引擎", "引擎状态", "描述"));
		for(Iterator<String> it = serviceMap.keySet().iterator(); it.hasNext(); ) {
			String key = it.next();
			ServiceStore service = serviceMap.get(key);
			System.out.println(String.format("%-20s\t%-20\t%-20s\t%-30s\t%s", key, "已启动", service.getRelationEngine(), service.getAutostart() ? "启用" : "未启用", service.getEngineDesc()));
		}
	}
	
	public void startAllService(){
		for(Iterator<String> it = serviceMap.keySet().iterator(); it.hasNext(); ) {
			String key = it.next();
			ServiceStore service = serviceMap.get(key);
			if (service.getAutostart()) {
				if(0 == service.start()) {
					if (service.getClass().getName().equals(CMPP20MtService.class.getName())) {
						service.getEngine().setParam("feeType", "02");
						service.getEngine().setParam("feeCode", "05");
						//service.getJedis().setParam(key, val);
					}
				}
				
			}
		}
	}
	
	public void stopAllService(){
		for(Iterator<String> it = serviceMap.keySet().iterator(); it.hasNext(); ) {
			String key = it.next();
			ServiceStore service = serviceMap.get(key);
			if (service.getAutostart()) {
				service.stop();
			}
		}
	}
	public static void main(String[] args) {
		/*
		Options options = new Options();
		options.addOption("s", true, "查询服务状态");
		//parse
        CommandLineParser parser = new BasicParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
            System.out.println(cmd.getOptionValue("s"));
        } catch (ParseException pe) {
            usage(options);
            return;
        }
		*/
		
		RunAllService runner = new RunAllService();
		System.out.print("smep>>");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			while(true) {
				String cmd = br.readLine();
				if ("service status".equals(cmd)) {
					runner.showService();
				} else if ("service start".equals(cmd)) {
					runner.startAllService();
				} else if ("service stop".equals(cmd)) {
					runner.stopAllService();
				} else if ("quit".equals(cmd) || "exit".equals(cmd)) {
					System.exit(0);
				}
				System.out.print("smep>>");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/*
    private static void usage(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("CLIDemo", options);
    }
	*/
}
