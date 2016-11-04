package org.ancode.alivelib.crash;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskPool {
	public static ExecutorService AN_TASK_EXECUTOR = (ExecutorService) Executors.newFixedThreadPool(10);
	public static ExecutorService SINGLE_TASK_EXECUTOR = (ExecutorService) Executors.newSingleThreadExecutor();
}
