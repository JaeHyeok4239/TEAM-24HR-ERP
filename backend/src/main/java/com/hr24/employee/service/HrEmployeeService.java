package com.hr24.employee.service;

import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hr24.employee.dto.hr.DepartmentTreeResponseDto;
import com.hr24.employee.dto.hr.DepartmentTreeResponseDto.DepartmentTreeNodeDto;
import com.hr24.employee.dto.hr.EmployeeCreateRequestDto;
import com.hr24.employee.dto.hr.EmployeeDetailResponseDto;
import com.hr24.employee.dto.hr.EmployeeFormOptionsResponseDto;
import com.hr24.employee.dto.hr.EmployeeListResponseDto;
import com.hr24.employee.entity.Department;
import com.hr24.employee.entity.Position;
import com.hr24.employee.entity.Role;
import com.hr24.employee.entity.User;
import com.hr24.employee.entity.UserRole;
import com.hr24.employee.enums.EmploymentType;
import com.hr24.employee.enums.UserStatus;
import com.hr24.employee.repository.DepartmentRepository;
import com.hr24.employee.repository.PositionRepository;
import com.hr24.employee.repository.RoleRepository;
import com.hr24.employee.repository.UserRepository;
import com.hr24.employee.repository.UserRoleRepository;
import com.hr24.global.exception.BusinessException;
import com.hr24.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HrEmployeeService {

    private static final String EMPLOYEE_NO_PREFIX = "EMP";
    private static final String DEFAULT_ROLE_CODE = "USER";
    private static final String ACTIVE_YN = "Y";
    private static final int MAX_EMPLOYEE_NO_SEQUENCE = 9999;

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public List<EmployeeListResponseDto> findEmployees(
            Long departmentId,
            UserStatus status,
            EmploymentType employmentType,
            String keyword
    ) {
        String normalizedKeyword = normalizeKeyword(keyword);

        return userRepository.searchHrEmployees(
                        departmentId,
                        status,
                        employmentType,
                        normalizedKeyword
                )
                .stream()
                .map(EmployeeListResponseDto::from)
                .toList();
    }

    public EmployeeDetailResponseDto findEmployeeDetail(Long employeeId) {
        User user = userRepository.findByEmployeeIdWithDepartmentAndPosition(employeeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        List<String> roles = findRoleCodes(user.getEmployeeId());

        return EmployeeDetailResponseDto.from(user, roles);
    }

    public DepartmentTreeResponseDto findDepartmentTree() {
        long totalRegularActiveEmployeeCount = userRepository.countByStatusAndEmploymentType(
                UserStatus.ACTIVE,
                EmploymentType.REGULAR
        );

        long totalDailyActiveEmployeeCount = userRepository.countByStatusAndEmploymentType(
                UserStatus.ACTIVE,
                EmploymentType.DAILY
        );

        List<Department> departments = departmentRepository.findAllActiveWithParentDepartment();

        Map<Long, DepartmentTreeNodeDto> nodeMap = new HashMap<>();
        Map<Long, List<DepartmentTreeNodeDto>> childrenMap = new HashMap<>();

        for (Department department : departments) {
            long regularActiveEmployeeCount =
                    userRepository.countByDepartment_DepartmentIdAndStatusAndEmploymentType(
                            department.getDepartmentId(),
                            UserStatus.ACTIVE,
                            EmploymentType.REGULAR
                    );

            DepartmentTreeNodeDto node = DepartmentTreeNodeDto.of(
                    department,
                    regularActiveEmployeeCount,
                    new ArrayList<>()
            );

            nodeMap.put(department.getDepartmentId(), node);
            childrenMap.put(department.getDepartmentId(), node.getChildren());
        }

        List<DepartmentTreeNodeDto> rootNodes = new ArrayList<>();

        for (Department department : departments) {
            DepartmentTreeNodeDto currentNode = nodeMap.get(department.getDepartmentId());

            Department parentDepartment = department.getParentDepartment();

            if (parentDepartment == null || !nodeMap.containsKey(parentDepartment.getDepartmentId())) {
                rootNodes.add(currentNode);
                continue;
            }

            List<DepartmentTreeNodeDto> parentChildren =
                    childrenMap.get(parentDepartment.getDepartmentId());

            parentChildren.add(currentNode);
        }

        return new DepartmentTreeResponseDto(
                totalRegularActiveEmployeeCount,
                totalDailyActiveEmployeeCount,
                rootNodes
        );
    }

    @Transactional
    public EmployeeDetailResponseDto createEmployee(EmployeeCreateRequestDto request) {
        validateDuplicateLoginId(request.getLoginId());
        validateDuplicateEmail(request.getEmail());

        Department department = null;
        Position position = null;

        if (request.getEmploymentType() == EmploymentType.REGULAR) {
            validateRegularEmployeeRequiredFields(request);

            department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.DEPARTMENT_NOT_FOUND));

            position = positionRepository.findById(request.getPositionId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.POSITION_NOT_FOUND));
        }

        String employeeNo = generateEmployeeNo();
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.createEmployee(
                employeeNo,
                request.getLoginId().trim(),
                encodedPassword,
                request.getName().trim(),
                request.getEmail().trim(),
                department,
                position,
                request.getEmploymentType()
        );

        User savedUser = userRepository.save(user);

        assignDefaultUserRole(savedUser);

        return EmployeeDetailResponseDto.from(
                savedUser,
                List.of(DEFAULT_ROLE_CODE)
        );
    }

    private void validateDuplicateLoginId(String loginId) {
        if (userRepository.existsByLoginIdIgnoreCase(loginId.trim())) {
            throw new BusinessException(ErrorCode.DUPLICATE_LOGIN_ID);
        }
    }

    private void validateDuplicateEmail(String email) {
        if (userRepository.existsByEmailIgnoreCase(email.trim())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }
    }

    private void validateRegularEmployeeRequiredFields(EmployeeCreateRequestDto request) {
        if (request.getDepartmentId() == null || request.getPositionId() == null) {
            throw new BusinessException(ErrorCode.INVALID_EMPLOYEE_CREATE_REQUEST);
        }
    }

    private String generateEmployeeNo() {
        String currentYear = String.valueOf(Year.now().getValue());
        String prefix = EMPLOYEE_NO_PREFIX + currentYear;

        int nextSequence = userRepository
                .findTopByEmployeeNoStartingWithOrderByEmployeeNoDesc(prefix)
                .map(User::getEmployeeNo)
                .map(employeeNo -> employeeNo.replace(prefix, ""))
                .map(Integer::parseInt)
                .orElse(0) + 1;

        if (nextSequence > MAX_EMPLOYEE_NO_SEQUENCE) {
            throw new BusinessException(ErrorCode.INVALID_EMPLOYEE_CREATE_REQUEST);
        }

        String employeeNo = prefix + String.format("%04d", nextSequence);

        if (userRepository.existsByEmployeeNo(employeeNo)) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMPLOYEE_NO);
        }

        return employeeNo;
    }

    private List<String> findRoleCodes(Long employeeId) {
        return userRoleRepository.findAllWithRoleByEmployeeId(employeeId)
                .stream()
                .map(userRole -> userRole.getRole().getRoleCode())
                .toList();
    }
    
    private void assignDefaultUserRole(User user) {
        Role role = roleRepository
                .findByRoleCodeAndIsActive(DEFAULT_ROLE_CODE, ACTIVE_YN)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND));

        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);

        userRoleRepository.save(userRole);
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }

        return keyword.trim();
    }
    
    public EmployeeFormOptionsResponseDto findEmployeeFormOptions() {
        List<EmployeeFormOptionsResponseDto.DepartmentOptionDto> departments =
                departmentRepository.findAllActiveWithParentDepartment()
                        .stream()
                        .map(EmployeeFormOptionsResponseDto.DepartmentOptionDto::from)
                        .toList();

        List<EmployeeFormOptionsResponseDto.PositionOptionDto> positions =
                positionRepository.findAll()
                        .stream()
                        .filter(position -> !"N".equals(position.getIsActive()))
                        .map(EmployeeFormOptionsResponseDto.PositionOptionDto::from)
                        .toList();

        return new EmployeeFormOptionsResponseDto(
                departments,
                positions
        );
    }
}