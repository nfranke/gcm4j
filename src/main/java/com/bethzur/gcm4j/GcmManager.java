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
package com.bethzur.gcm4j;

import java.io.IOException;

/**
 * An interface for pushing messages to clients via the GCM service.
 * Configuration details (e.g., providing the authentication token) are
 * implementation-dependent.
 * <p>
 * Implementations do not implement automatic retry, exponential back off, or
 * attempt to honor <code>Retry-After</code> headers. For many applications, the
 * asynchronous manager {@link AsyncGcmManager}, which implements these
 * features on top of a {@code GcmManager} instance, will be more appropriate.
 *
 * @see DefaultGcmManager
 * @see AsyncGcmManager
 *
 * @author David R. Bild
 *
 */
public interface GcmManager {

	/**
	 * Sends a message to the GCM service to be delivered to the client
	 * specified in the message header.
	 *
	 * @param msg
	 *            the message to deliver
	 * @return the response from the GCM service
	 * @throws UnexpectedResponseException
	 *             if the GCM service response could not be parsed
	 * @throws AuthTokenException
	 *             if authentication token could not be retrieved
	 * @throws IOException
	 *             if unable to communicate with the GCM service
	 */
	public Response pushMessage(Message msg)
			throws UnexpectedResponseException, IOException;

}
