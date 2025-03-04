package org.mamba.mapper;

import org.apache.ibatis.annotations.*;
import org.mamba.entity.Lecturer;
import java.util.List;

/**
 * Mapper interface for database operations on the Lecturer entity.
 */
@Mapper
public interface LecturerMapper {
    /**
     * Inserts a new Lecturer record.
     * @param lecturer Lecturer entity object
     */
    @Insert("INSERT INTO Lecturer (email, Uid, name, phone) VALUES (#{email}, #{uid}, #{name}, #{phone})")
    void insert(Lecturer lecturer);

    /**
     * Finds a Lecturer record by email.
     * @param email Lecturer's email
     * @return Corresponding Lecturer entity object
     */
    @Select("SELECT * FROM Lecturer WHERE email = #{email}")
    Lecturer findByEmail(String email);

    /**
     * Retrieves all Lecturer records.
     * @return List of Lecturer entity objects
     */
    @Select("SELECT * FROM Lecturer")
    List<Lecturer> findAll();

    /**
     * Updates a Lecturer record.
     * @param lecturer Lecturer entity object to be updated
     */
    @Update("UPDATE Lecturer SET name = #{name}, phone = #{phone} WHERE email = #{email}")
    void update(Lecturer lecturer);

    /**
     * Deletes a Lecturer record by email.
     * @param email Lecturer's email
     */
    @Delete("DELETE FROM Lecturer WHERE email = #{email}")
    void delete(String email);
}
