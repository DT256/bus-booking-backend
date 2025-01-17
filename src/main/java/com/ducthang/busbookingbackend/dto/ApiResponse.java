package com.ducthang.busbookingbackend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder // Thêm Builder pattern để tiện dụng hơn khi tạo object
@JsonInclude(JsonInclude.Include.NON_NULL) // Bỏ qua các trường null khi serialize thành JSON
public class ApiResponse<T> {

    private String status;
    private int code; // Mã phản hồi, ví dụ 200 (OK), 400 (Bad Request), 500 (Server Error)
    private String message; // Thông điệp ngắn gọn mô tả kết quả
    private T data; // Dữ liệu trả về (nếu có)
    private Object errors; // Chi tiết lỗi (nếu có)

    /**
     * Tạo response thành công.
     *
     * @param data    Dữ liệu trả về
     * @param message Thông điệp mô tả
     * @return ApiResponse<T>
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .status("success")
                .code(200)
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Tạo response lỗi.
     *
     * @param responseCode Mã lỗi
     * @param message      Thông điệp lỗi
     * @param errors       Thông tin chi tiết lỗi
     * @return ApiResponse<T>
     */
    public static <T> ApiResponse<T> error(int responseCode, String message, Object errors) {
        return ApiResponse.<T>builder()
                .status("error")
                .code(responseCode)
                .message(message)
                .errors(errors)
                .build();
    }
}