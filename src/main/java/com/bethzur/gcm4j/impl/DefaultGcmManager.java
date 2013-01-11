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
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.bethzur.gcm4j.GcmManager;
import com.bethzur.gcm4j.Message;
import com.bethzur.gcm4j.Response;
import com.bethzur.gcm4j.UnexpectedResponseException;
import com.bethzur.gcm4j.auth.ApiKeyAuthProvider;

/**
 * The default implementation of {@link GcmManager} for pushing messages to
 * clients via the GCM service. The instance is configured via the constructor,
 * which takes an {@link AuthTokenProvider} for retrieving and persisting
 * authentication tokens and an {@link HttpClient} used to the send HTTP
 * requests to the GCM servers.
 * <p>
 * This class is thread-safe only if the provided {@link HttpClient} instance is
 * thread-safe. An instance which is safe up to {@code MAX_THREADS} concurrent
 * threads (i.e., sufficient for a thread-pool of size {@code MAX_THREADS}) can
 * be obtained like this:<br/>
 * <code>
 * &nbsp;&nbsp;&nbsp;&nbsp; ThreadSafeClientConnManager connManager = new ThreadSafeClientConnManager(); <br/>
 * &nbsp;&nbsp;&nbsp;&nbsp; conmManager.setMaxTotal(MAX_THREADS); <br/>
 * &nbsp;&nbsp;&nbsp;&nbsp; connManager.setDefaultMaxPerRoute(MAX_THREADS); <br/>
 * &nbsp;&nbsp;&nbsp;&nbsp; HttpClient client = new DefaultHttpClient(connManager); <br/>
 * </code>
 * <p>
 * SLF4J is used for logging.
 *
 * @author David R. Bild
 *
 */
public class DefaultGcmManager implements GcmManager {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(DefaultGcmManager.class);

	private static final String URI_STRING = "https://android.googleapis.com/gcm/send";
	private static final URI URI;
	static {
		try {
			URI = new URI(URI_STRING);
		} catch (URISyntaxException e) {
			LOGGER.debug("Failed to create URI.", e);
			throw new RuntimeException(String.format(
					"Default URI is invalid: %s", URI_STRING), e);

		}
	}

	private final ApiKeyAuthProvider keyProvider;

	private final HttpClient httpClient;

	/**
	 * Constructs a new DefaultGcmManager using the specified
	 * {@code HttpClient} to send HTTP requests and {@code AuthTokenProvider} to
	 * retrieve and persist the authentication token. The instance is
	 * thread-safe only if the {@code HttpClient} instance is. See
	 * {@link DefaultGcmManager} for details.
	 *
	 * @param httpClient
	 *            the client used to send HTTP requests
	 *
	 * @param tokenProvider
	 *            the token provider used to retrieve and persist authentication
	 *            tokens
	 */
	public DefaultGcmManager(HttpClient httpClient,
			ApiKeyAuthProvider keyProvider) {
		this.keyProvider = keyProvider;
		this.httpClient = httpClient;
	}

	@Override
	public Response pushMessage(Message msg) throws IOException,
			UnexpectedResponseException {
		LOGGER.debug("Sending GCM message: {}", msg);
		Response response = httpClient.execute(new GcmHttpPost(msg,
				keyProvider.getKey(), URI),
				new GcmHttpResponseHandler(msg));
		LOGGER.debug("Received GCM reponse: {}", response);
		return response;
	}

}
