package edu.kozhinov.enjoyit.core;

import edu.kozhinov.enjoyit.core.async.Asynchronous;

import java.net.Socket;

public interface AsyncSocketHolder extends Asynchronous {
    Socket getSocket();
}
