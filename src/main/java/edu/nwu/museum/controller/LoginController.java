package edu.nwu.museum.controller;

import edu.nwu.museum.common.authentication.JWTUtil;
import edu.nwu.museum.common.authentication.Response;
import edu.nwu.museum.common.exception.UnauthorizedException;
import edu.nwu.museum.domain.User;
import edu.nwu.museum.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@RestController
public class LoginController {
  @Autowired
  private UserService userService;

  @RequestMapping(value = "/logout")
  public String logout(){
    //使用权限管理工具进行用户的退出，跳出登录，给出提示信息
    Subject subject = SecurityUtils.getSubject();
    if (subject.isAuthenticated()) {
      log.info("Logout success.");
      subject.logout();
    }
    return "登出成功";
  }

  @CrossOrigin
  @RequestMapping(value = "/login", method = RequestMethod.POST)
  public Response login(@RequestParam("userId") String userId,
      @RequestParam("password") String password) {
    User user = userService.findById(userId);
    if(user.getPassword().equals(password)) {
      return new Response(200, "Login success.", JWTUtil.sign(userId, password));
    } else {
      throw new UnauthorizedException();
    }
  }

  @RequestMapping(value = "/article", method = RequestMethod.GET)
  public Response article() {
    Subject subject = SecurityUtils.getSubject();
    if (subject.isAuthenticated()) {
      return new Response(200, "You have already logged in.", null);
    } else {
      return new Response(200, "You are a guest.", null);
    }
  }

  @RequestMapping(value = "/require-role", method = RequestMethod.GET)
  @RequiresRoles("admin")
  public Response requireRole() {
    return new Response(200, "You are visiting /require-role.", null);
  }

  @RequestMapping(value = "/require-permission", method = RequestMethod.GET)
  @RequiresPermissions(logical = Logical.AND, value = {"add", "delete"})
  public Response requirePermission() {
    return new Response(200, "You are visiting /require-permission.", null);
  }

  @RequestMapping(path = "/401")
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public Response unauthorized() {
    return new Response(401, "Unauthorized", null);
  }
}
