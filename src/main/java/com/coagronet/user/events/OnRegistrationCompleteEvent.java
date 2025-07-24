package com.coagronet.user.events;

import org.springframework.context.ApplicationEvent;
import com.coagronet.user.User;

public class OnRegistrationCompleteEvent extends ApplicationEvent {

	private final User user;

	public OnRegistrationCompleteEvent(User user) {
		super(user);
		this.user = user;
	}

	public User getUser() {
		return user;
	}

}
