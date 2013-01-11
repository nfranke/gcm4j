/*
 * Copyright 2012 The Regents of the University of Michigan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bethzur.gcm4j.impl;

import com.bethzur.gcm4j.Message;
import com.bethzur.gcm4j.ResponseType;
import com.bethzur.gcm4j.SuccessResponse;

/**
 * Implementation of {@link SuccessResponse} used by {@link DefaultGcmManager}.
 *
 * @author David R. Bild
 *
 */
class SuccessResponseImpl extends ResponseImpl implements SuccessResponse {

	private final String sentMessageId;
	private final String registrationId;

	/**
	 * Constructs a new response with the specified sent message id and
	 * associated message.
	 *
	 * @param sentMessageId
	 *            the sent message id
	 * @param message
	 *            the message for which this is a response
	 */
	public SuccessResponseImpl(String sentMessageId, String registrationId, Message message) {
		super(ResponseType.Success, message);
		this.sentMessageId = validateSentMessageId(sentMessageId);
		this.registrationId = registrationId; // Can be null
	}

	private String validateSentMessageId(String sentMessageId) {
		if (sentMessageId == null)
			throw new IllegalArgumentException(
					"Argument 'sentMessageId' may not be null.");

		return sentMessageId;
	}

	@Override
	public String getSentMessageId() {
		return sentMessageId;
	}

	@Override
	public String getRegistrationId() {
		return registrationId;
	}

	@Override
	public String toString() {
		return String
				.format("SuccessResponseImpl(type=%s, sentMessageId=%s, registrationId=%s, message=%s)",
						this.getResponseType(), this.getSentMessageId(), this.getRegistrationId(),
						this.getMessage());
	}
}
