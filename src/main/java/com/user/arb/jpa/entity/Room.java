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

    @Column(nullable = false, length = 20, unique = true)
    private String color;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Schedule> schedules = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
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
