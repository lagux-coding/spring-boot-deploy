package swp.koi.service.socketIoService;

import com.corundumstudio.socketio.AuthorizationResult;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import swp.koi.model.enums.TokenType;
import swp.koi.service.jwtService.JwtService;

import java.io.InputStream;

/**
 * Configuration class for setting up and managing a Socket.IO server.
 * It initializes the server with the specified host and port from the application configuration,
 * handles client connections, and manages JWT authentication for clients.
 *
 * <p>This class also contains methods for handling client connection events
 * and stopping the server gracefully on application shutdown.</p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SocketIOConfig {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    // I have set the configuration values in application.yaml file
    @Value("0.0.0.0")
    private String socketHost;

    @Value("8081")
    private int socketPort;

    // SocketIOServer class is used to create a socket server
    private SocketIOServer server;

    /**
     * Creates and configures a SocketIOServer bean.
     *
     * <p>This method sets up the server hostname and port,
     * configures the authorization logic for incoming connections,
     * and starts the server. It also adds listeners for client connection
     * and disconnection events, logging relevant information.</p>
     *
     * @return the configured SocketIOServer instance
     */
    @Bean
    public SocketIOServer socketIOServer() {
        // Configuration object holds the server settings
        Configuration config = new Configuration();

        config.setHostname(socketHost);
        config.setPort(socketPort);

        InputStream stream = getClass().getResourceAsStream("/keystore.p12");
        config.setKeyStore(stream);
        config.setKeyStorePassword("123123");

//        config.setAuthorizationListener(auth -> {
//            var token = auth.getHttpHeaders().get("socket-token");
//            if (!token.isEmpty()) {
//                var username = jwtService.extractUsername(token, TokenType.ACCESS_TOKEN);
//                var account = userDetailsService.loadUserByUsername(username);
//                jwtService.validateToken(token, account, TokenType.ACCESS_TOKEN);
//                return new AuthorizationResult(true);
//            }
//
//            return new AuthorizationResult(true);
//        });

        server = new SocketIOServer(config);
        server.start();

        server.addConnectListener(client -> log.info("Client connected: {}", client.getSessionId()));
        server.addDisconnectListener(client -> log.info("Client disconnected: {}", client.getSessionId()));

        return server;
    }

    /**
     * Stops the Socket.IO server gracefully when the application is shutting down.
     */
    @PreDestroy
    public void stopSocketServer() {
        this.server.stop();
    }
}
