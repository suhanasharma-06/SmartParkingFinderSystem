package parking.database;

import java.sql.*;

public class SlotUpdater {

    public static void releaseExpiredSlots() {
        try(Connection con = DBConnection.getConnection()) {

            String selectSql = "SELECT parking_location, slot_number FROM bookings WHERE end_time <= NOW() AND status='booked'";
            PreparedStatement ps = con.prepareStatement(selectSql);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                String location = rs.getString("parking_location");
                String slot = rs.getString("slot_number");

                PreparedStatement ps2 = con.prepareStatement(
                    "UPDATE slots SET status='available' WHERE location_name=? AND slot_number=?"
                );
                ps2.setString(1, location);
                ps2.setString(2, slot);
                ps2.executeUpdate();

                PreparedStatement ps3 = con.prepareStatement(
                    "UPDATE parking_locations SET available_slots = available_slots + 1, occupied_slots = occupied_slots - 1 WHERE location_name=?"
                );
                ps3.setString(1, location);
                ps3.executeUpdate();
            }

            PreparedStatement ps4 = con.prepareStatement(
                "UPDATE bookings SET status='completed' WHERE end_time <= NOW() AND status='booked'"
            );
            ps4.executeUpdate();

        } catch(Exception ex){
            ex.printStackTrace();
        }
    }
}