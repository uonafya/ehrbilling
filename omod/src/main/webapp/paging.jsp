
<c:set var="moduleId" value="ehrbilling" />
<c:set var="baseLink" value="${pagingUtil.baseLink}" />
<c:set var="pageSize" value="${pagingUtil.pageSize}" />
<c:set var="currentPage" value="${pagingUtil.currentPage}" />
<c:set var="startPage" value="${pagingUtil.startPage}" />
<c:set var="numberOfPages" value="${pagingUtil.numberOfPages}" />
<%-- Error screen appears on clicking next page or changing page size in list of bills --%>
<c:set var="patientId" value="${pagingUtil.patientId}" />

<input type="hidden" id="baseLink" value="${baseLink}" />
<input type="hidden" id="currentPage" value="${currentPage}" />
<%--  Error screen appears on clicking next page or changing page size in list of bills --%>
<input type="hidden" id="patientId" value="${patientId}" />
<c:if test="${numberOfPages > 0 }">
	<ul class="pageSizeSelection">
		<li><span><spring:message
					code="${moduleId}.paging.totalpage" />:</span> ${numberOfPages}</li>
		<li><span><spring:message
					code="${moduleId}.paging.pagesize" />:</span> <input type="text"
			id="sizeOfPage" value="${pageSize}" style="width: 50px"
			onchange="changePageSize('${baseLink}');">
		</li>
		<li><span><spring:message
					code="${moduleId}.paging.jumptopage" />:</span> <input type="text"
			id="jumpToPage" value="${currentPage}" style="width: 50px"
			onchange="jumpPage('${baseLink}');">
		</li>
	</ul>
	<div class="paging">
		<c:choose>
			<c:when test="${currentPage > 1}">
				<c:set var="prev" value="${currentPage - 1 }" />
				<%-- Error screen appears on clicking next page or changing page size in list of bills --%>
				<a href="${baseLink}currentPage=1&pageSize=${pageSize}&patientId=${patientId}"
					class="first" title="First">&laquo;&laquo;</a>
				<a href="${baseLink}currentPage=${prev}&pageSize=${pageSize}&patientId=${patientId}"
					class="prev" title="Previous">&laquo;</a>
			</c:when>
			<c:otherwise>
				<span class="first" title="First">&laquo;&laquo;</span>
				<span class="prev" title="Previous">&laquo;</span>
			</c:otherwise>
		</c:choose>
		<c:forEach begin="0" end="4" step="1" var="i">
			<c:set var="p" value="${startPage + i }" />
			<c:if test="${p <= numberOfPages }">
				<c:if test="${i > 0}">
					<span class="seperator">|</span>
				</c:if>
				<c:choose>
					<c:when test="${p != currentPage }">
					<%-- Error screen appears on clicking next page or changing page size in list of bills --%>
						<a href="${baseLink}currentPage=${p}&pageSize=${pageSize}&patientId=${patientId}"
							class="page" title="Page $p">${p}</a>
					</c:when>
					<c:otherwise>
						<span class="page" title="Page $p">${p}</span>
					</c:otherwise>
				</c:choose>
			</c:if>
		</c:forEach>
		<c:choose>
			<c:when test="${currentPage < numberOfPages  }">
				<c:set var="next" value="${currentPage + 1  }" />
				<%-- Error screen appears on clicking next page or changing page size in list of bills --%>
				<a href="${baseLink}currentPage=${next}&pageSize=${pageSize}&patientId=${patientId}"
					class="next" title="Next">&raquo;</a>
				<a
					href="${baseLink}currentPage=${numberOfPages}&pageSize=${pageSize}&patientId=${patientId}"
					class="last" title="Last">&raquo;&raquo;</a>
			</c:when>
			<c:otherwise>
				<span class="next" title="Next">&raquo; </span>
				<span class="last" title="Last">&raquo;&raquo;</span>
			</c:otherwise>
		</c:choose>
	</div>
</c:if>
