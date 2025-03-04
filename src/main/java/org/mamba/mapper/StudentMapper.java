package org.mamba.mapper;

import org.apache.ibatis.annotations.*;
import org.mamba.entity.Student;
import java.util.List;

@Mapper
public interface StudentMapper {
    @Insert("INSERT INTO Student (email, Uid, name, phone, breakTimer) VALUES (#{email}, #{uid}, #{name}, #{phone}, #{breakTimer})")
    void insert(Student student);

    @Select("SELECT * FROM Student WHERE email = #{email}")
    Student findByEmail(String email);

    @Select("SELECT * FROM Student")
    List<Student> findAll();

    @Update("UPDATE Student SET name = #{name}, phone = #{phone}, breakTimer = #{breakTimer} WHERE email = #{email}")
    void update(Student student);

    @Delete("DELETE FROM Student WHERE email = #{email}")
    void delete(String email);
}