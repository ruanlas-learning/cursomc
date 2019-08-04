package com.example.cursomc.services.exceptions;

public class FileStoragePathInvalidException extends RuntimeException {

	public FileStoragePathInvalidException(String message) {
        super(message);
    }

    public FileStoragePathInvalidException(String message, Throwable cause) {
        super(message, cause);
    }
}
