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

/**
 * Encapsulates a response from the GCM service. This interface includes
 * methods for accessing information common to all responses: type, message,
 * and, if included, updated authorization token.
 * <p>
 * Some response types include additional information. These are encapsulated by
 * subclasses: {@link SuccessResponse} and {@link UnavailableResponse}. The
 * specific (sub)class associated with any {@link ResponseType} can be retrieved
 * via {@link ResponseType#associatedClass()}.
 *
 * @see ResponseType
 * @see SuccessResponse
 * @see UnavailableResponse
 *
 * @author David R. Bild
 *
 */
public interface Response {
	/**
	 * Gets the type of this response.
	 *
	 * @return the type of this response.
	 */
	public ResponseType getResponseType();

	/**
	 * Retrieves the associated message (i.e., that passed to
	 * {@link GcmManager#pushMessage(Message)}) for this response.
	 *
	 * @return the associated response
	 */
	public Message getMessage();
}
