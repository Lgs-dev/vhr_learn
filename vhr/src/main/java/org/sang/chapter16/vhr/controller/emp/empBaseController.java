package org.sang.chapter16.vhr.controller.emp;

import org.sang.chapter16.vhr.entity.Employee;
import org.sang.chapter16.vhr.service.EmpolyeeService;
import org.sang.chapter16.vhr.utils.POIUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lgs
 * @date 2021-03-29 16:36
 */
@RestController
@RequestMapping("/employee/basic")
public class empBaseController {

    @Autowired
    EmpolyeeService empolyeeService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public Map<String, Object> getEmployeeByPage(@RequestParam(defaultValue = "1") Integer page,
                                                 @RequestParam(defaultValue = "10") Integer size,
                                                 Employee employee, Date[] beginDateScope){
        Map<String, Object> map = new HashMap<>();
        if(employee ==null){
            employee = new Employee();
        }
        map = empolyeeService.getEmployeeByPage( page, size,employee,beginDateScope );
        return map;
    }
    @GetMapping("/export")
    public ResponseEntity<byte[]> export(){
        List<Employee> list = (List<Employee>) empolyeeService.getEmployeeByPage( null,null,new Employee(),null ).get( "data" );
        return POIUtils.employee2Excel( list );
    }

//    @PostMapping("/import")
//    public Map<String,Object> importData(MultipartFile file) throws IOException{
//        List<Employee> list = POIUtils.excel2Employee( file, nationService.getAllNations(), politicsstatusService.getAllPoliticsstatus(), departmentService.getAllDepartmentsWithOutChildren(), positionService.getAllPositions(), jobLevelService.getAllJobLevels());
//        Map<String, Object> map = new HashMap<>();
//        if(empolyeeService.addEmps(list) == list.size()){
//            map.put( "msg","上传成功！");
//            return map;
//        }
//        map.put( "msg","上传失败！");
//        return map;
//
//    }


}
