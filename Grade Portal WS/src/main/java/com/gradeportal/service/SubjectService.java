package com.gradeportal.service;

import com.gradeportal.entity.Subject;
import com.gradeportal.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SubjectService {
    
    @Autowired
    private SubjectRepository subjectRepository;
    
    public Subject createSubject(String name, String description) {
        if (subjectRepository.existsByName(name)) {
            throw new RuntimeException("Subject with this name already exists");
        }
        
        Subject subject = new Subject();
        subject.setName(name);
        subject.setDescription(description);
        
        return subjectRepository.save(subject);
    }
    
    public List<Subject> findAllSubjects() {
        return subjectRepository.findAll();
    }
    
    public List<Subject> findSubjectsByName(String name) {
        return subjectRepository.findByNameContainingIgnoreCase(name);
    }
    
    public Optional<Subject> findById(Long id) {
        return subjectRepository.findById(id);
    }
    
    public Optional<Subject> findByName(String name) {
        return subjectRepository.findByName(name);
    }
    
    public void deleteSubject(Long id) {
        subjectRepository.deleteById(id);
    }
    
    public Subject updateSubject(Long id, String name, String description) {
        Optional<Subject> subjectOpt = subjectRepository.findById(id);
        if (subjectOpt.isPresent()) {
            Subject subject = subjectOpt.get();
            subject.setName(name);
            subject.setDescription(description);
            return subjectRepository.save(subject);
        }
        throw new RuntimeException("Subject not found");
    }
}
