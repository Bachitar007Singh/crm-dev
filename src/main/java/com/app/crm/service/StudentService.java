
package com.app.crm.service;

import com.app.crm.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    // Calculate male percentage
    public double calculateMalePercentage() {
        long maleCount = studentRepository.countMaleStudents();
        long totalStudents = studentRepository.count();
        return totalStudents == 0 ? 0 : (maleCount * 100.0) / totalStudents;
    }

    // Calculate female percentage
    public double calculateFemalePercentage() {
        long femaleCount = studentRepository.countFemaleStudents();
        long totalStudents = studentRepository.count();
        return totalStudents == 0 ? 0 : (femaleCount * 100.0) / totalStudents;
    }

    // Calculate average age
    public int calculateAverageAge() {
        List<Integer> ages = studentRepository.getAllAges();
        if (ages.isEmpty()) {
            return 0;
        }
        int sum = ages.stream().mapToInt(Integer::intValue).sum();
        return sum / ages.size();
    }

    // Get age distribution (e.g., counts for age ranges 18-20, 21-23, 24-26)
    public List<Integer> getAgeDistribution() {
        List<Integer> ages = studentRepository.getAllAges();
        int count18to20 = 0;
        int count21to23 = 0;
        int count24to26 = 0;

        for (int age : ages) {
            if (age >= 18 && age <= 20) {
                count18to20++;
            } else if (age >= 21 && age <= 23) {
                count21to23++;
            } else if (age >= 24 && age <= 26) {
                count24to26++;
            }
        }

        return List.of(count18to20, count21to23, count24to26);
    }
}