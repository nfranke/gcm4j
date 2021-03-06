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
package com.bethzur.gcm4j.async;

import java.util.concurrent.ScheduledExecutorService;

import org.apache.http.client.HttpClient;
import com.bethzur.gcm4j.GcmManager;
import com.bethzur.gcm4j.async.handler.AsyncHandlers;
import com.bethzur.gcm4j.async.handler.AsyncHandlersFactory;
import com.bethzur.gcm4j.auth.ApiKeyAuthProvider;
import com.bethzur.gcm4j.impl.DefaultGcmManager;

/**
 * Provides static methods for creating {@link AsyncGcmManager} instances.
 *
 * @author David R. Bild
 *
 */
public class AsyncGcmManagerFactory {

	/**
	 * Should not be instantiated.
	 */
	private AsyncGcmManagerFactory() {
		throw new IllegalStateException();
	}

	/**
	 * Creates an {@code AsyncGcmManager} instance that uses a
	 * {@link ScheduledExecutorService} to deliver messages via a
	 * {@link GcmManager}. {@link AsyncHandlers} are used to filter the
	 * messages and automatically handle to responses and exceptions.
	 * <p>
	 * The given {@code GcmManager} must be thread-safe for as many threads as
	 * the {@code ScheduleExecutorService} will run concurrently.
	 * <p>
	 * An executor for {@code MAX_THREADS} concurrent threads can created like
	 * this: <code> </br>
	 * ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(MAX_THREADS);
	 * </code>
	 *
	 * @param manager
	 *            the synchronous manager for communicating with the GCM
	 *            service
	 * @param handlers
	 *            the handlers for automatically handling responses and
	 *            exceptions
	 * @param executor
	 *            the executor providing the background threads to deliver
	 *            queued messages
	 * @return the constructed async manager
	 */
	public static AsyncGcmManager create(GcmManager manager,
			AsyncHandlers handlers, ScheduledExecutorService executor) {
		return new AsyncGcmManagerImpl(manager, handlers, executor);
	}

	/**
	 * Creates an {@code AsyncGcmManager} instance that uses a
	 * {@link ScheduledExecutorService} to deliver messages via a
	 * {@link GcmManager}.
	 * <p>
	 * A default {@link AsyncHandlers} instance that implements automatic retry
	 * with exponential back-off (globally for <code>Service Unavailable</code>
	 * and <code>Quota
	 * Exceeded</code> errors and per-device for
	 * <code>Device Quota Exceeded</code> errors) and honors
	 * <code>Retry-After</code> headers is registered.
	 * <p>
	 * The given {@code GcmManager} must be thread-safe for as many threads as
	 * the {@code ScheduleExecutorService} might run concurrently.
	 * <p>
	 * An executor for {@code MAX_THREADS} concurrent threads can created like
	 * this: <code> </br>
	 * ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(MAX_THREADS);
	 * </code>
	 *
	 * @param manager
	 *            the synchronous manager for communicating with the GCM
	 *            service
	 * @param executor
	 *            the executor providing the background threads to deliver
	 *            queued messages
	 * @return the constructed async manager
	 */
	public static AsyncGcmManager create(GcmManager manager,
			ScheduledExecutorService executor) {
		return create(manager, AsyncHandlersFactory.create(), executor);
	}

	/**
	 * Creates an {@code AsyncGcmManager} instance that uses a
	 * {@link ScheduledExecutorService} to deliver messages via an
	 * {@link GcmManager} instance constructed from the given
	 * {@link HttpClient} and {@link AuthTokenProvider}. {@link AsyncHandlers}
	 * are used to filter the messages and automatically handle to responses and
	 * exceptions.
	 * <p>
	 * The given {@code HttpClient} must be thread-safe for as many threads as
	 * the {@code ScheduleExecutorService} will run concurrently. An
	 * {@code HttpClient} instance for {@code MAX_THREADS} concurrent threads
	 * can be created like this: </br> <code>
	 *   ThreadSafeClientConnManager connManager = new ThreadSafeClientConnManager(); </br>
	 *   conmManager.setMaxTotal(MAX_THREADS); </br>
	 *   connManager.setDefaultMaxPerRoute(MAX_THREADS); </br>
	 *   HttpClient client = new DefaultHttpClient(connManager); </br>
	 * </code>
	 * <p>
	 * An executor for {@code MAX_THREADS} concurrent threads can created like
	 * this: </br> <code>
	 *   ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(MAX_THREADS);
	 * </code>
	 *
	 * @param client
	 *            the client used to send HTTP requests
	 * @param provider
	 *            the provider used to retrieve and persist authentication
	 *            tokens
	 * @param handlers
	 *            the handlers for automatically handling responses and
	 *            exceptions
	 * @param executor
	 *            the executor providing the background threads to deliver
	 *            queued messages
	 * @return the constructed async manager
	 */
	public static AsyncGcmManager create(HttpClient client,
			ApiKeyAuthProvider provider, AsyncHandlers handlers,
			ScheduledExecutorService executor) {
		return create(new DefaultGcmManager(client, provider), handlers,
				executor);
	}

	/**
	 * Creates an {@code AsyncGcmManager} instance that uses a
	 * {@link ScheduledExecutorService} to deliver messages via an
	 * {@link GcmManager} instance constructed from the given
	 * {@link HttpClient} and {@link AuthTokenProvider}.
	 * <p>
	 * A default {@link AsyncHandlers} instance that implements automatic retry
	 * with exponential back-off (globally for <code>Service Unavailable</code>
	 * and <code>Quota
	 * Exceeded</code> errors and per-device for
	 * <code>Device Quota Exceeded</code> errors) and honors
	 * <code>Retry-After</code> headers is registered.
	 * <p>
	 * The given {@code HttpClient} must be thread-safe for as many threads as
	 * the {@code ScheduleExecutorService} will run concurrently. An
	 * {@code HttpClient} instance for {@code MAX_THREADS} concurrent threads
	 * can be created like this: </br> <code>
	 *   ThreadSafeClientConnManager connManager = new ThreadSafeClientConnManager(); </br>
	 *   conmManager.setMaxTotal(MAX_THREADS); </br>
	 *   connManager.setDefaultMaxPerRoute(MAX_THREADS); </br>
	 *   HttpClient client = new DefaultHttpClient(connManager); </br>
	 * </code>
	 * <p>
	 * An executor for {@code MAX_THREADS} concurrent threads can created like
	 * this: </br> <code>
	 *   ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(MAX_THREADS);
	 * </code>
	 *
	 * @param client
	 *            the client used to send HTTP requests
	 * @param provider
	 *            the provider used to retrieve and persist authentication
	 *            tokens
	 * @param executor
	 *            the executor providing the background threads to deliver
	 *            queued messages
	 * @return the constructed async manager
	 */
	public static AsyncGcmManager create(HttpClient client,
			ApiKeyAuthProvider provider, ScheduledExecutorService executor) {
		return create(client, provider, AsyncHandlersFactory.create(), executor);
	}

}
