package org.openmrs.module.epts.etl.utilities.concurrent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Keeps borrowed resources alive until every asynchronous user has finished.
 */
public final class TaskCompletion {
	private TaskCompletion() {
	}

	public static <T> T await(CompletableFuture<T> completion) throws InterruptedException, ExecutionException {
		try {
			return completion.get();
		} catch (InterruptedException e) {
			completion.handle((result, failure) -> null).join();
			Thread.currentThread().interrupt();

			throw e;
		}
	}
}
