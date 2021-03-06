package com.ecom.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ecom.serviceImpl.UserSecurityImpl;

import io.jsonwebtoken.ExpiredJwtException;


@Component
public class JwtAuthFilter extends OncePerRequestFilter {
	
	@Autowired
	private UserSecurityImpl usersecurity;
	
	 @Autowired
	   private JwtUtils jwtutils;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		
		final String requestTokentHeader =request.getHeader("Authorization");
		
		String username=null;
		String jwtToken=null;
		
		if(requestTokentHeader!=null && requestTokentHeader.startsWith("Bearer"))
		{
			
			jwtToken =requestTokentHeader.substring(7);
			
			try {
				username=this.jwtutils.extractUsername(jwtToken);
			} catch (ExpiredJwtException e) {
				
				e.printStackTrace();
				System.out.println("jwt token has expired");
			}
			
		}else {
			System.out.println("Invalid token,not start bearer" );
		}
		
		
		//validated
		
		
		
		if(username!=null && SecurityContextHolder.getContext().getAuthentication()==null)
		{
			final UserDetails userDetails = usersecurity.loadUserByUsername(username);
		      if(jwtutils.validateToken(jwtToken, userDetails));
		      {
		    	  UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
		    	  usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		    	  SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
		      }
		}else {
			System.out.println("Tokent is not Valid");
		}
		
		filterChain.doFilter(request, response);
	
		
	}

	 
}
