package com.github.musicsnsproject.config.web;

import com.github.musicsnsproject.common.security.userdetails.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Component
public class CurrentUserModelAdvice {

	@ModelAttribute("loginUser")
	public Map<String, Object> loginUser(@AuthenticationPrincipal Object principal) {
		
		Map<String, Object> map = new HashMap<>();

		if (principal instanceof CustomUserDetails) {
			
			CustomUserDetails user = (CustomUserDetails) principal;

			String name  = firstNonBlank(safe(user.getNickname()), safe(user.getUsername()));
			String email = safe(user.getEmail());

			map.put("name",  name);
			map.put("email", email);
			
			return map;
		}

		map.put("name",  "");
		map.put("email", "");
		
		return map;
	}

	private static String safe(String s) {
		return s == null ? "" : s;
	}

	private static String firstNonBlank(String... vals) {
		if (vals == null) return "";
		for (String v : vals) {
			if (v != null && !v.isBlank()) return v;
		}
		return "";
	}
}
