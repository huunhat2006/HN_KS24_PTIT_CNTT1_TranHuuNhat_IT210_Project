package com.restaurant.hnks24cntt1it210tranhuunhatproject.service.impl;

        import com.restaurant.hnks24cntt1it210tranhuunhatproject.dto.UserLoginDTO;
        import com.restaurant.hnks24cntt1it210tranhuunhatproject.dto.UserRegisterDTO;
        import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.User;
        import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.UserProfile;
        import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.enums.UserRole;
        import com.restaurant.hnks24cntt1it210tranhuunhatproject.repository.UserProfileRepository;
        import com.restaurant.hnks24cntt1it210tranhuunhatproject.repository.UserRepository;
        import com.restaurant.hnks24cntt1it210tranhuunhatproject.service.AuthService;
        import lombok.RequiredArgsConstructor;
        import lombok.extern.slf4j.Slf4j;
        import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
        import org.springframework.stereotype.Service;
        import org.springframework.transaction.annotation.Transactional;

        @Service
        @RequiredArgsConstructor
        @Slf4j
        public class AuthServiceImpl implements AuthService {

            private final UserRepository userRepository;
            private final UserProfileRepository userProfileRepository;
            private final BCryptPasswordEncoder passwordEncoder;

            @Override
            @Transactional
            public User register(UserRegisterDTO userRegisterDTO) {
                if (userRepository.existsByUsername(userRegisterDTO.getUsername())) {
                    throw new RuntimeException("Username đã tồn tại");
                }

                if (userProfileRepository.existsByEmail(userRegisterDTO.getEmail())) {
                    throw new RuntimeException("Email đã tồn tại");
                }

                if (userRegisterDTO.getStudentIdCode() != null && !userRegisterDTO.getStudentIdCode().isBlank()
                        && userProfileRepository.existsByStudentIdCode(userRegisterDTO.getStudentIdCode())) {
                    throw new RuntimeException("Mã sinh viên đã tồn tại");
                }

                if (userRegisterDTO.getRole() == UserRole.ADMIN) {
                    throw new RuntimeException("Tài khoản đăng ký chỉ được là STUDENT hoặc LECTURER");
                }

                User user = User.builder()
                        .username(userRegisterDTO.getUsername())
                        .passwordHash(passwordEncoder.encode(userRegisterDTO.getPassword()))
                        .role(userRegisterDTO.getRole())
                        .build();

                User savedUser = userRepository.save(user);

                UserProfile userProfile = UserProfile.builder()
                        .user(savedUser)
                        .fullName(userRegisterDTO.getFullName())
                        .studentIdCode(userRegisterDTO.getStudentIdCode())
                        .email(userRegisterDTO.getEmail())
                        .phone(userRegisterDTO.getPhone())
                        .build();

                userProfileRepository.save(userProfile);
                savedUser.setUserProfile(userProfile);

                return savedUser;
            }

            @Override
            @Transactional(readOnly = true)
            public User login(UserLoginDTO userLoginDTO) {
                log.info("Attempting login for username: {}", userLoginDTO.getUsername());

                User user = userRepository.findByUsername(userLoginDTO.getUsername())
                        .orElseThrow(() -> {
                            log.warn("User not found with username: {}", userLoginDTO.getUsername());
                            return new RuntimeException("Sai tài khoản hoặc mật khẩu");
                        });

                log.info("User found: {} with passwordHash length: {}", user.getUsername(), user.getPasswordHash().length());
                log.debug("Password hash from DB: {}", user.getPasswordHash());

                boolean passwordMatches = passwordEncoder.matches(userLoginDTO.getPassword(), user.getPasswordHash());
                log.info("Password match result: {}", passwordMatches);

                if (!passwordMatches) {
                    log.warn("Password mismatch for user: {}", userLoginDTO.getUsername());
                    throw new RuntimeException("Sai tài khoản hoặc mật khẩu");
                }

                log.info("Login successful for user: {}", user.getUsername());
                return user;
            }
        }