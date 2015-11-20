package com.aesirteam.smep.adc.simulator;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class CharsetFilter implements Filter {
	
	protected String encoding; 
	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		this.encoding = null;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		// TODO Auto-generated method stub
		/*如果从web.xml中读取的编码格式不为空，则对request设置编码格式*/  
		if (this.encoding != null)  
		{  
			request.setCharacterEncoding(this.encoding);//设置编码格式  
			/*这里只是在控制台输出验证是否加载了过滤器，并从web.xml中读取到了正确的编码格式*/   
		}  
		/*继续执行程序*/  
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		this.encoding = filterConfig.getInitParameter("encoding");
	}

}
