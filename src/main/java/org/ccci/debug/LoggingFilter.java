package org.ccci.debug;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.ccci.util.HttpRequests;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.log.Log;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;


/**
 * Logs interesting things about the request, so request boundaries are easier to see.
 * 
 * Logs the request path at debug level, and logs parameters at trace level 
 * 
 * If some parameters are sensitive, then a {@link ParameterSanitizer} can 
 * be used to mask certain parameter values.
 * 
 * 
 * @author Matt Drees
 */
@Name("loggingFilter")
@Scope(ScopeType.APPLICATION)
@BypassInterceptors
@org.jboss.seam.annotations.web.Filter(within = "org.jboss.seam.web.ajax4jsfFilter")
public class LoggingFilter implements Filter {

	@Logger Log log;
	
	ParameterSanitizer parameterSanitizer;
	
	/**
	 * Contains a list of regular expressions that specify which urls should ignored by this logging filter.
	 * Some urls are hit very frequently (for example, a page that is checked by a monitoring or loadbalancing system).
	 */
	private List<String> urlPatternsToIgnore = Lists.newArrayList();
	
	public void destroy() 
	{
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException 
	{
		if (request instanceof HttpServletRequest){
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			String fullPath = HttpRequests.getFullPath(httpRequest);
			boolean ignore = false;
			for (String urlPatternToIgnore : urlPatternsToIgnore)
			{
			    //possible optimization: pre-compile these into a single regex
			    if (fullPath.matches(urlPatternToIgnore))
			    {
			        ignore = true;
			    }
			}
			if (!ignore)
			{
			    log.debug(httpRequest.getMethod() + " request for path #0", fullPath);
			    if (log.isTraceEnabled() && httpRequest.getParameterNames().hasMoreElements()) {
			        log.trace("with parameters: #0", getSanitizedHashedRequest(httpRequest).toString());
			    }
			}
		}
		chain.doFilter(request, response);
	}


	public void init(FilterConfig filterConfig) throws ServletException 
	{
	    this.parameterSanitizer = (ParameterSanitizer) Component.getInstance("parameterSanitizer");
	    Preconditions.checkNotNull(this.parameterSanitizer, "parameterSanitizer is not available");
	}

	private Map<String, String> getSanitizedHashedRequest(HttpServletRequest req) 
	{
		Map<String, String> requestCopy = new LinkedHashMap<String, String>();
		
		for (@SuppressWarnings("unchecked") //HttpServletRequest is not generic
		     Enumeration<String> parameterNames = (Enumeration<String>) req.getParameterNames(); 
			 parameterNames.hasMoreElements(); ) 
		{
			String parameterName = parameterNames.nextElement();
            List<String> sanitizedParameterValues =
                    parameterSanitizer.sanitizeParameter(parameterName,
                        Arrays.asList(req.getParameterValues(parameterName)));
		    requestCopy.put(parameterName, sanitizedParameterValues.toString());
		}
		return requestCopy;
	}


    public List<String> getUrlPatternsToIgnore()
    {
        return urlPatternsToIgnore;
    }

    public void setUrlPatternsToIgnore(List<String> urlPatternsToIgnore)
    {
        Preconditions.checkNotNull(urlPatternsToIgnore, "urlPatternsToIgnore is null");
        this.urlPatternsToIgnore = urlPatternsToIgnore;
    }
    
	
	
}
