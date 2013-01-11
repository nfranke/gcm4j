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

import java.util.Date;

import com.bethzur.gcm4j.Message;
import com.bethzur.gcm4j.ResponseType;
import com.bethzur.gcm4j.UnavailableResponse;

/**
 * Implementation of {@link UnavailableResponse} used by
 * {@link DefaultGcmManager}.
 *
 * @author David R. Bild
 *
 */
class UnavailableResponseImpl extends ResponseImpl implements
		UnavailableResponse {

	private final Date retryAfter;

	/**
	 * Constructs a new response for the specified retry-after header and
	 * associated message.
	 *
	 * @param retryAfter
	 *            the retry-after header value
	 * @param message
	 *            the message for which this is a response
	 */
	public UnavailableResponseImpl(Date retryAfter, Message message) {
		super(ResponseType.ServiceUnavailable, message);
		this.retryAfter = retryAfter;
	}

	@Override
	public boolean hasRetryAfter() {
		return (retryAfter != null);
	}

	@Override
	public Date retryAfter() {
		return retryAfter;
	}

	@Override
	public String toString() {
		return String
				.format("UnavailableResponseImpl(type=%s, retryAfter=\"%s\", message=%s)",
						this.getResponseType(), this.retryAfter(),
						this.getMessage());
	}

}
