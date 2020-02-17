package com.code.bms.controller;

import com.code.bms.annotation.LoginRequired;
import com.code.bms.common.DataGridDataSource;
import com.code.bms.common.JsonData;
import com.code.bms.common.PageBean;
import com.code.bms.model.Role;
import com.code.bms.service.PermissionService;
import com.code.bms.service.RoleService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: luhailiang
 * @Date: 2019-03-28 20:19
 * @Description:
 */
@RestController
@RequestMapping("/role")
public class RoleController {

    @Resource
    private RoleService roleService;


    @Resource
    private PermissionService permissionService;


    /**
     * @param role
     * @description: 新增角色
     */
    @PostMapping("/save")
    @LoginRequired
    public JsonData saveRole(Role role) {
        int count = roleService.saveRole(role);
        if (count > 0) {
            return JsonData.success(count, "添加成功");
        } else {
            return JsonData.fail("添加失败");
        }

    }


    /**
     * @param role
     * @description: 更新角色
     */
    @PutMapping("/update")
    @LoginRequired
    public JsonData updateRole(Role role) {
        int count = roleService.updateRole(role);
        if (count > 0) {
            return JsonData.success(count, "更新成功");
        } else {
            return JsonData.fail("更新失败");
        }
    }


    /**
     * @param roleId
     * @description: 删除角色(先根据角色id删除角色权限关联信息, 再根据角色id删除用户角色关联信息)
     */
    @DeleteMapping("/delete")
    @LoginRequired
    public JsonData deleteRole(@RequestParam(value = "roleId") Integer roleId) {

        //TODO 根据角色id删除角色权限关联信息，再根据角色id删除用户角色关联信息
        permissionService.deleteRolePermissionRsByRoleId(roleId);
        roleService.deleteRoleUserRsByRoleId(roleId);
        int count = roleService.deleteRole(roleId);
        if (count > 0) {
            return JsonData.success(count, "删除成功");
        } else {
            return JsonData.fail("删除失败");
        }
    }


    /**
     * @description: 服务端分页查询角色列表
     */
    @PostMapping("/list")
    @LoginRequired
    public DataGridDataSource<Role> getRoleList(@RequestParam(value = "roleName", required = false, defaultValue = "") String roleName,
                                                @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                @RequestParam(value = "rows", required = false, defaultValue = "5") Integer rows) {
        PageBean pageBean = new PageBean(page, rows);
        Map<String, Object> map = new HashMap<>();
        map.put("roleName", "%" + roleName + "%");
        map.put("start", pageBean.getStart());
        map.put("size", pageBean.getPageSize());
        List<Role> roleList = roleService.selectRoleList(map);
        int totalRole = roleService.getTotalRole(map);
        DataGridDataSource<Role> dataGridDataSource = new DataGridDataSource<>();
        dataGridDataSource.setRows(roleList);
        dataGridDataSource.setTotal(totalRole);
        return dataGridDataSource;
    }


    /**
     *
     * @param roleId
     * @param permissionIds
     * @description: 角色权限设置(先删除当前角色拥有的权限关系, 再重新设置)
     */
    @PostMapping("/savePermissionSet")
    @LoginRequired
    public JsonData savePermissionSet(Integer roleId, Integer[] permissionIds) {
        //先删除当前角色拥有的权限关系
        permissionService.deleteRolePermissionRsByRoleId(roleId);
        Map<String, Object> map = new HashMap<>();
        map.put("roleId", roleId);
        map.put("permissionIds", permissionIds);
        int count = roleService.insertRolePermissions(map);
        if (count > 0) {
            return JsonData.success(count, "设置成功");
        } else {
            return JsonData.fail("设置失败");
        }

    }

}
