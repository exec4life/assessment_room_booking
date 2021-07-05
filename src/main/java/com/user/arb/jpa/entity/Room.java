package com.user.arb.jpa.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tbl_room")
public class Room extends AbstractEntity implements Serializable, Cloneable  {

    @Column(nullable = false, length = 20, unique = true)
    private String name;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Booking> bookings = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Room)) return false;
        return Objects.equals(getName(), ((Room) obj).getName());
    }

    @Override
    public Room clone() throws CloneNotSupportedException {
        return (Room) super.clone();
    }

    @Override
    public String toString() {
        return String.join("#", new String[]{
                getId().toString(), getName(), getActive().toString()
        });
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
