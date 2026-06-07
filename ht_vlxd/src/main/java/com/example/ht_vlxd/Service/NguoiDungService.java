package com.example.ht_vlxd.Service;

import com.example.ht_vlxd.Model.NguoiDung;
import com.example.ht_vlxd.Repository.NguoiDungRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NguoiDungService {
    private final NguoiDungRepository nguoiDungRepository;
    private final PasswordEncoder passwordEncoder;

    public NguoiDungService(NguoiDungRepository nguoiDungRepository, PasswordEncoder passwordEncoder) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<NguoiDung> getAllUsers() {
        return nguoiDungRepository.findAll();
    }

    public NguoiDung findByUsername(String username) {
        return nguoiDungRepository.findByUsername(username);
    }

    public NguoiDung findBySoDienThoai(String soDienThoai) {
        return nguoiDungRepository.findBySoDienThoai(soDienThoai);
    }

    public List<NguoiDung> findByHoTen(String hoTen) {
        return nguoiDungRepository.findByHoTen(hoTen);
    }

    public NguoiDung registerUser(NguoiDung user) {
        NguoiDung existing = nguoiDungRepository.findByUsername(user.getUsername());
        if (existing != null) {
            return null;
        }
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        return nguoiDungRepository.save(user);
    }

    public NguoiDung save(NguoiDung user) {
        return nguoiDungRepository.save(user);
    }

    public void deleteUser(Long id) {
        nguoiDungRepository.deleteById(id);
    }
}
