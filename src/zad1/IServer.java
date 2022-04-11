package zad1;

import java.io.IOException;

public interface IServer {
    void connection() throws IOException;
    void message() throws IOException;
    void disconnect() throws IOException;
}
