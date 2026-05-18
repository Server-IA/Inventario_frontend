package com.coagronet.user.events;

import org.springframework.context.ApplicationEvent;
import com.coagronet.user.User;

public class OnRegistrationCompleteEvent extends ApplicationEvent {

	private static final long serialVersionUID = 6909953127916060942L;

	private final User user;
	private final String fallbackLanguageTag;

	public OnRegistrationCompleteEvent(User user, String fallbackLanguageTag) {
		super(user);
		this.user = user;
		this.fallbackLanguageTag = fallbackLanguageTag;
	}

	public User getUser() {
		return user;
	}

	public String getFallbackLanguageTag() {
		return fallbackLanguageTag;
	}

}
