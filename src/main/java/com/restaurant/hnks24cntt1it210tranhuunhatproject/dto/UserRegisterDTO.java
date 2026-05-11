package com.restaurant.hnks24cntt1it210tranhuunhatproject.dto;

    import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.enums.UserRole;
    import jakarta.validation.constraints.Email;
    import jakarta.validation.constraints.NotBlank;
    import jakarta.validation.constraints.NotNull;
    import jakarta.validation.constraints.Size;
    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public class UserRegisterDTO {

        @NotBlank(message = "Username không được để trống")
        private String username;

        @NotBlank(message = "Mật khẩu không được để trống")
        @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
        private String password;

        @NotBlank(message = "Họ và tên không được để trống")
        private String fullName;

        private String studentIdCode;

        @NotBlank(message = "Email không được để trống")
        @Email(message = "Email không hợp lệ")
        private String email;

        private String phone;

        @NotNull(message = "Vui lòng chọn vai trò")
        private UserRole role;
    }