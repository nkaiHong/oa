package org.fkjava.identity.controller;

import java.util.List;

import org.fkjava.identity.domain.Role;
import org.fkjava.identity.domain.User;
import org.fkjava.identity.service.IdentityService;
import org.fkjava.identity.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/identity/user")
@SessionAttributes({ "modifyUserId" })
public class UserController {

	@Autowired
	private IdentityService identityService;
	@Autowired
	private RoleService roleService;

	// Model是通常放到方法参数列表中的，用于控制器和JSP传值，也可以作为方法返回值
	// View只是用来返回页面，可以作为返回值
	// ModelAndView是方法返回值，包含了数据和视图
	@GetMapping
	public ModelAndView index(//
			@RequestParam(name = "pageNumber", defaultValue = "0") Integer number, // 页码
			@RequestParam(name = "keyword", required = false) String keyword// 搜索的关键字
	//
	) {
		ModelAndView mav = new ModelAndView("identity/user/index");

		// 查询一页的数据
		Page<User> page = identityService.findUsers(keyword, number);
		mav.addObject("page", page);

		return mav;
	}

	@GetMapping("/add")
	public ModelAndView add() {
		ModelAndView mav = new ModelAndView("identity/user/add");
		// 后面这里需要查询数据，因为每个用户都有【身份】、【角色】，在修改用户信息的时候，需要选择用户的身份。
		List<Role> roles = this.roleService.findAllNotFixed();
		mav.addObject("roles", roles);

		return mav;
	}

	@PostMapping
	public String save(User user, //
			// 从Session里面获取要修改的用户的ID
			@SessionAttribute(value = "modifyUserId", required = false) String modifyUserId, //
			SessionStatus sessionStatus//
	) {
		// 修改用户的时候，把用户的ID设置到User对象里面
		if (modifyUserId != null
				// user对象没有id表示新增，新增的时候不需要id
				&& !StringUtils.isEmpty(user.getId())) {
			user.setId(modifyUserId);
		}
		identityService.save(user);

		// 清理现场、Session里面的modifyUserId
		sessionStatus.setComplete();

		return "redirect:/identity/user";
	}

	@GetMapping("/{id}")
	public ModelAndView detail(@PathVariable("id") String id) {
		// 页面跟添加一样，只是需要把User根据id查询出来
		ModelAndView mav = this.add();

		User user = this.identityService.findUserById(id);
		mav.addObject("user", user);
		// 把要修改的用户的ID存储在Session里面，避免在浏览器恶意修改！
		mav.addObject("modifyUserId", user.getId());

		return mav;
	}

	@GetMapping("/active/{id}")
	public String active(@PathVariable("id") String id) {
		this.identityService.active(id);
		return "redirect:/identity/user";
	}

	@GetMapping("/disable/{id}")
	public String disable(@PathVariable("id") String id) {
		this.identityService.disable(id);
		return "redirect:/identity/user";
	}
}
