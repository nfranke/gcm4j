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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import com.bethzur.gcm4j.Message;
import com.bethzur.gcm4j.Response;
import com.bethzur.gcm4j.ResponseType;
import com.bethzur.gcm4j.UnexpectedResponseException;

/**
 * A handler responsible for parsing GCM http responses to construct
 * {@link ResponseImpl}, {@link SuccesssResponseImpl}, and
 * {@link UnavailableResponseImpl} objects encapsulating them.
 *
 * @author David R. Bild
 *
 */
class GcmHttpResponseHandler implements ResponseHandler<Response> {
	private static final Pattern SPLITTER = Pattern.compile("=");

	final Message message;

	public GcmHttpResponseHandler(Message message) {
		this.message = message;
	}

	@Override
	public Response handleResponse(HttpResponse response) throws IOException {
		switch (response.getStatusLine().getStatusCode()) {
		case 200:
			List<NameValuePair> body = parseBody(response);
			ResponseType type = getResponseType(body);
			switch (type) {
			case Success:
				return new SuccessResponseImpl(getResponse(body, ResponseKeys.id), getResponse(body, ResponseKeys.registration_id), message);
			default:
				return new ResponseImpl(type, message);
			}
		case 500:
			return new ResponseImpl(ResponseType.InternalError, message);
		case 503:
			Date retryAfter = getRetryAfter(response);
			return new UnavailableResponseImpl(retryAfter, message);
//		case 400: // JSON
		case 401:
			return new ResponseImpl(ResponseType.Unauthorized, message);
		default:
			throw new UnexpectedResponseException(String.format(
					"Unexpected HTTP status code: %d", response.getStatusLine()
							.getStatusCode()));
		}
	}

	private Date getRetryAfter(HttpResponse response) {
		Header retryAfterHeader = response.getFirstHeader("Retry-After");
		if (retryAfterHeader != null) {
			// Read as HTTP-Date
			try {
				return org.apache.http.impl.cookie.DateUtils
						.parseDate(retryAfterHeader.getValue());
			} catch (DateParseException e) {
			}

			// Read as seconds
			try {
				return new Date(System.currentTimeMillis() + 1000L
						* Integer.valueOf(retryAfterHeader.getValue()));
			} catch (NumberFormatException e) {
			}
		}

		// Otherwise
		return null;
	}

	private List<NameValuePair> parseBody(HttpResponse response)
			throws UnexpectedResponseException {
		try {
			String body_lines = EntityUtils.toString(response.getEntity());
//			body_lines += "\nregistration_id=xxx";
//			System.out.println(body_lines);

			String lines[] = body_lines.split("[\r\n]+");

			List<NameValuePair> result = new ArrayList<NameValuePair>();
			for (String body : lines) {
				String[] splitBody = SPLITTER.split(body);
				if (splitBody.length == 2) {
					result.add(new BasicNameValuePair(splitBody[0], splitBody[1]));
				} else {
					throw new UnexpectedResponseException(String.format(
							"Unexpected format of message body:\n%s", body));
				}
			}
			return result;
		} catch (ParseException e) {
			throw new UnexpectedResponseException(e);
		} catch (IOException e) {
			throw new UnexpectedResponseException(e);
		}
	}

	private String getResponse(List<NameValuePair> body_list, ResponseKeys find) {
		try {
			for (NameValuePair body : body_list) {
				if (ResponseKeys.valueOf(body.getName()) == find) {
					return body.getValue();
				}
			}
		} catch (IllegalArgumentException e) {
		}
		return null;
	}

	private ResponseType getResponseType(List<NameValuePair> body_list)
			throws UnexpectedResponseException {
		try {
			for (NameValuePair body : body_list) {
				switch (ResponseKeys.valueOf(body.getName())) {
				case id:
					return ResponseType.Success;
				case registration_id:
					// Ignore for now.
					break;
				case Error:
					switch (ResponseErrorValues.valueOf(body.getValue())) {
					case QuotaExceeded:
						return ResponseType.QuotaExceeded;
					case DeviceQuotaExceeded:
						return ResponseType.DeviceQuotaExceeded;
					case MissingRegistration:
						return ResponseType.MissingRegistration;
					case InvalidRegistration:
						return ResponseType.InvalidRegistration;
					case MismatchSenderId:
						return ResponseType.MismatchSenderId;
					case NotRegistered:
						return ResponseType.NotRegistered;
					case MessageTooBig:
						return ResponseType.MessageTooBig;
					case MissingCollapseKey:
						return ResponseType.MissingCollapseKey;
					default:
						throw new UnexpectedResponseException(
								"Unexpected error message.");
					}
				default:
					throw new UnexpectedResponseException(
							"Unexpected key in body name-value pair.");
				}
			}
		} catch (IllegalArgumentException e) {
		}
		throw new UnexpectedResponseException(
				"Unexpected format in message.");
	}

	/**
	 * Keys used in the {@code 200} responses from the GCM service.
	 *
	 * @author David R. Bild
	 *
	 */
	static enum ResponseKeys {
		id, registration_id, Error
	}

	/**
	 * Possible values for the {@code Error} key in {@code 200} responses from
	 * the GCM service.
	 *
	 * @author David R. Bild
	 *
	 */
	static enum ResponseErrorValues {
		QuotaExceeded, DeviceQuotaExceeded, MissingRegistration, InvalidRegistration, MismatchSenderId, NotRegistered, MessageTooBig, MissingCollapseKey
	}
}
