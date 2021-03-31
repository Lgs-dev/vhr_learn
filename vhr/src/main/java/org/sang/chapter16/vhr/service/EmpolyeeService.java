package org.sang.chapter16.vhr.service;

import org.sang.chapter16.vhr.entity.Employee;
import org.sang.chapter16.vhr.mapper.EmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lgs
 * @date 2021-03-29 16:43
 */
@Service
public class EmpolyeeService {
    @Autowired
    EmployeeMapper employeeMapper;

    public Map<String, Object> getEmployeeByPage(Integer page, Integer size, Employee employee, Date[] beginDateScope){
        if(page !=null && size != null){
            page = (page -1) * size;
        }
        List<Employee> data = employeeMapper.getEmployeeByPage(page,size,employee,beginDateScope);
        Long total  = employeeMapper.getTotal(employee, beginDateScope);
        Map<String, Object> map = new HashMap<>();
        map.put( "data",data );
        map.put( "total", total);
        return map;
    }



}
