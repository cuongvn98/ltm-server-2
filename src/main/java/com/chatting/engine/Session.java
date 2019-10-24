package com.chatting.engine;

import com.chatting.engine.interfaces.User;

import java.util.HashSet;
import java.util.Set;

public class Session {
    private User user;
    private Set<Connection> connections = new HashSet<>();
    private Set<Room> rooms = new HashSet<>();
    private String status = SessionStatus.idle;
    private boolean persistStatus = false;

    public Session(User user) {
        this.user = user;
    }

    public String getUsername() {
        return user.getUsername();
    }

    public User getUser() {
        return user;
    }

    public void send(String message) {
        connections.forEach(con -> con.send(message));
    }

    public void addConnection(Connection con) {
        this.connections.add(con);
        if (!status.equals(SessionStatus.online)) {
            setStatus(SessionStatus.online);
        }
    }

    public void removeConnection(Connection con) {
        this.connections.remove(con);
        if (this.connections.size() == 0) {
            waitReconnect();
        }
    }

    private void waitReconnect() {
        this.setStatus(SessionStatus.idle);
        Thread thread = new IdleThread(this);
        thread.start();
    }

    void join(Room room) {
        this.rooms.add(room);
    }

    void leave(Room room) {
        this.rooms.remove(room);
    }

    void onClose() {
        for (Room room : rooms) {
            room.leave(this);
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (!isPersistStatus()) {
            this.status = status;
        }
    }

    private boolean isPersistStatus() {
        return this.persistStatus;
    }

    public void setPersistStatus(String status) {
        if (status == SessionStatus.online) {
            persistStatus = false;
        } else {
            persistStatus = true;
        }
        this.status = status;
    }

    public int getConnections() {
        return this.connections.size();
    }
}
