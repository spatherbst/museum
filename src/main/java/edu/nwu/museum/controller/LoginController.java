package edu.nwu.museum.controller;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;

@RestController
public class LoginController {
  @RequestMapping("/login")
  public String login() {
    return "请输入用户id与密码进行登录";
  }

  @RequestMapping("/")
  public String redirectIndex() {
    return "返回首页";
  }

  @RequestMapping("/test")
  public String test() {
    return "测试页面";
  }

  @CrossOrigin
  @RequestMapping(value = "/login", method = RequestMethod.POST)
  public String login(String userId, String password, Boolean rememberMe) {
    // 暂时先用明文来存储密码
    UsernamePasswordToken token = new UsernamePasswordToken(userId, password, rememberMe);
    Subject subject = SecurityUtils.getSubject();
    try {
      subject.login(token);
      return "登录成功";
    } catch (UnknownAccountException e) {
      return "登录失败";
    } catch (IncorrectCredentialsException e) {
      return "登录失败";
    } catch (LockedAccountException e) {
      return "登录失败";
    } catch (AuthenticationException e) {
      return "登录失败";
    }
  }
}
