<%@ tag language="java" pageEncoding="UTF-8"%>
<%-- 这里可以写JSP的任何代码，并且可以定义接收参数！ -->
<%-- tag指令是JSP 2.0的时候提供的一个标签库指令，把HTML内容作为标签库来使用，避免在Java代码嵌入HTML -->
<%-- attribute指令用于在JSP Tag文件中定义参数 --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath }" scope="application"></c:set>
<%@ attribute name="url" required="true" type="java.lang.String" %>
<%@ attribute name="page" required="true" type="org.springframework.data.domain.Page" %>
<nav aria-label="分页导航">
    <ul class="pagination">
        <li>
            <a href="${ctx }${url }?pageNumber=${page.number eq 0 ? 0 : page.number - 1}" aria-label="上一页">
                <span aria-hidden="true">&laquo;</span>
            </a>
        </li>
        <c:set var="begin" value="${page.number - 2 }"></c:set>
        <c:set var="end" value="${page.number + 2 }"></c:set>
        <c:if test="${begin lt 0 }">
        	<%-- 此时begin是负数，在begin之前加上减号，就变成正数 --%>
        	<%-- end + begin取反，其实就是把end往右边挪一点 --%>
        	<c:set var="end" value="${end + (-begin)}"></c:set>
        	<c:set var="begin" value="0"></c:set>
        </c:if>
        <c:if test="${end gt (page.totalPages - 1) }">
        	<%-- 最大页码不能超出总页数 --%>
        	<c:set var="end" value="${page.totalPages - 1}"></c:set>
        	<c:set var="begin" value="${end - 4 }"></c:set>
        </c:if>
        <c:if test="${begin lt 0 }">
        	<c:set var="begin" value="0"></c:set>
        </c:if>

        <c:forEach begin="${begin }" end="${end }" var="number">
        	<li class="${page.number eq number ? 'active' : '' }"><a href="${ctx }${url }?pageNumber=${number}">${number + 1 }</a></li>
        </c:forEach>
        <li>
            <a href="${ctx }${url }?pageNumber=${page.number ge (page.totalPages - 1) ? page.totalPages - 1 : page.number + 1}" aria-label="下一页">
                <span aria-hidden="true">&raquo;</span>
            </a>
        </li>
    </ul>
</nav>