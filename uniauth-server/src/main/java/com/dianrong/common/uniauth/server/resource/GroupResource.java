package com.dianrong.common.uniauth.server.resource;

import com.codahale.metrics.annotation.Timed;
import com.dianrong.common.uniauth.common.bean.Response;
import com.dianrong.common.uniauth.common.bean.dto.GroupDto;
import com.dianrong.common.uniauth.common.bean.dto.PageDto;
import com.dianrong.common.uniauth.common.bean.dto.RoleDto;
import com.dianrong.common.uniauth.common.bean.dto.TagDto;
import com.dianrong.common.uniauth.common.bean.dto.UserDto;
import com.dianrong.common.uniauth.common.bean.dto.UserGroupDto;
import com.dianrong.common.uniauth.common.bean.request.GroupParam;
import com.dianrong.common.uniauth.common.bean.request.GroupQuery;
import com.dianrong.common.uniauth.common.bean.request.PrimaryKeyParam;
import com.dianrong.common.uniauth.common.bean.request.UserGroupParam;
import com.dianrong.common.uniauth.common.bean.request.UserListParam;
import com.dianrong.common.uniauth.server.service.GroupService;
import com.dianrong.common.uniauth.server.support.audit.ResourceAudit;
import com.dianrong.common.uniauth.server.support.tree.TreeType;
import com.dianrong.common.uniauth.server.support.tree.TreeTypeTag;
import com.dianrong.common.uniauth.sharerw.interfaces.IGroupRWResource;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@TreeTypeTag(TreeType.NORMAL)
@Api("组操作相关接口")
@RestController
public class GroupResource implements IGroupRWResource {

  @Autowired
  private GroupService groupService;

  @ApiOperation("根据查询条件分页查询组信息")
  @ApiImplicitParams(value = {
      @ApiImplicitParam(name = "pageNumber", value = "页码", required = true, dataType = "integer", paramType = "query"),
      @ApiImplicitParam(name = "pageSize", value = "每页大小", required = true, dataType = "integer", paramType = "query"),
      @ApiImplicitParam(name = "tenancyId", value = "租户id(或租户code)", required = true, dataType = "long", paramType = "query"),
      @ApiImplicitParam(name = "userGroupType", value = "查询条件:传入用户id与组关联关系(0:普通关联关系,1:用户是组的owner)", dataType = "integer", paramType = "query", allowableValues = "0,1"),
      @ApiImplicitParam(name = "userId", value = "查询条件:限定查询组的范围为与userId有关联关系", dataType = "long", paramType = "query"),
      @ApiImplicitParam(name = "roleId", value = "查询条件:限定查询组的范围为与roleId有关联关系", dataType = "long", paramType = "query"),
      @ApiImplicitParam(name = "tagId", value = "查询条件:限定查询组的范围为与tagId有关联关系", dataType = "long", paramType = "query"),
      @ApiImplicitParam(name = "id", value = "组id", dataType = "long", paramType = "query"),
      @ApiImplicitParam(name = "groupIds", value = "组id列表", dataType = "java.util.List", paramType = "query"),
      @ApiImplicitParam(name = "name", value = "组名称", dataType = "string", paramType = "query"),
      @ApiImplicitParam(name = "code", value = "组code", dataType = "string", paramType = "query"),
      @ApiImplicitParam(name = "description", value = "组描述", dataType = "string", paramType = "query"),
      @ApiImplicitParam(name = "status", value = "状态", dataType = "string", paramType = "query", allowableValues = "0, 1"),
      @ApiImplicitParam(name = "needTag", value = "是否将关联的tag信息一块查出来", dataType = "boolean", paramType = "query"),
      @ApiImplicitParam(name = "needUser", value = "是否将关联的user信息一块查出来", dataType = "boolean", paramType = "query"),
      @ApiImplicitParam(name = "needParentId", value = "是否将父组id也查出来", dataType = "boolean", paramType = "query")})
  @Override
  @Timed
  public Response<PageDto<GroupDto>> queryGroup(GroupQuery groupQuery) {
    PageDto<GroupDto> groupDtoPageDto = groupService.searchGroup(groupQuery.getUserGroupType(),
        groupQuery.getUserId(), groupQuery.getRoleId(), groupQuery.getId(),
        groupQuery.getGroupIds(), groupQuery.getName(), groupQuery.getCode(),
        groupQuery.getDescription(), groupQuery.getStatus(), groupQuery.getTagId(),
        groupQuery.getNeedTag(), groupQuery.getNeedUser(), groupQuery.getNeedParentId(),
        groupQuery.getPageNumber(), groupQuery.getPageSize());
    return Response.success(groupDtoPageDto);
  }

  @ApiOperation(value = "根据条件以树的形式返回组信息")
  @ApiImplicitParams(value = {
      @ApiImplicitParam(name = "tenancyId", value = "租户id(或租户code)", required = true, dataType = "long", paramType = "query"),
      @ApiImplicitParam(name = "id", value = "根组id(如果和code都不传，则查整棵树)", dataType = "long", paramType = "query"),
      @ApiImplicitParam(name = "code", value = "根组code(如果和id都不传，则查整棵树)", dataType = "string", paramType = "query"),
      @ApiImplicitParam(name = "onlyShowGroup", value = "是否只显示组信息(不然将会返回最关联的用户和标签信息)", dataType = "boolean", paramType = "query"),
      @ApiImplicitParam(name = "userGroupType", value = "用户与组的关联关系(0,1),在onlyShowGroup=false的时候必传", dataType = "integer", paramType = "query", allowableValues = "0,1"),
      @ApiImplicitParam(name = "roleId", value = "返回树中每个组该roleId的关联关系", dataType = "long", paramType = "query"),
      @ApiImplicitParam(name = "tagId", value = "返回树中每个组该tagId的关联关系", dataType = "long", paramType = "query"),
      @ApiImplicitParam(name = "needOwnerMarkup", value = "是否将userId与树中每个组的owner关系返回", dataType = "boolean", paramType = "query"),
      @ApiImplicitParam(name = "opUserId", value = "查询组与该userId的owner关系", dataType = "long", paramType = "query"),
      @ApiImplicitParam(name = "includeDisableUser", value = "当onlyShowGroup=false时,用于指定返回的用户列表是否包含禁用用户", dataType = "boolean", paramType = "query", defaultValue = "false"),})
  @Override
  @Timed
  public Response<GroupDto> getGroupTree(GroupParam groupParam) {
    GroupDto grpDto = groupService
        .getGroupTree(groupParam.getId(), groupParam.getCode(), groupParam.getOnlyShowGroup(),
            groupParam.getUserGroupType(), groupParam.getRoleId(),
            groupParam.getTagId(), groupParam.getNeedOwnerMarkup(), groupParam.getOpUserId(),
            groupParam.getIncludeDisableUser());
    return Response.success(grpDto);
  }

  @ApiOperation(value = "根据组Id或者Code查询组信息")
  @ApiImplicitParams(value = {
      @ApiImplicitParam(name = "id", value = "根组id", dataType = "long", paramType = "query"),
      @ApiImplicitParam(name = "code", value = "根组code(如果组id不传，则根据code查询)", dataType = "string", paramType = "query"),
      @ApiImplicitParam(name = "userGroupType", value = "用户与组的关联关系(0,1)", dataType = "integer", paramType = "query", allowableValues = "0,1"),
      @ApiImplicitParam(name = "userStatus", value = "用户是否启用(0,1)", dataType = "integer", paramType = "query", allowableValues = "0,1"),})
  @Override
  public Response<List<GroupDto>> getGroupTreeByIdOrCode(GroupParam groupParam) {
    List<GroupDto> groupDtos = groupService
        .getGroupTreeByIdOrCode(groupParam.getId(), groupParam.getCode(),
            groupParam.getUserGroupType(), groupParam.getUserStatus());
    return Response.success(groupDtos);
  }

  @ResourceAudit
  @ApiOperation(value = "添加用户与组的关联关系")
  @ApiImplicitParams(value = {
      @ApiImplicitParam(name = "groupId", value = "组id", required = true, dataType = "long", paramType = "query"),
      @ApiImplicitParam(name = "userIds", value = "用户列表", required = true, dataType = "java.util.List", paramType = "query"),
      @ApiImplicitParam(name = "normalMember", value = "普通关联关系(或owner关系)", dataType = "boolean", paramType = "query", defaultValue = "true"),})
  @Override
  public Response<Void> addUsersIntoGroup(UserListParam userListParam) {
    groupService.addUsersIntoGroup(userListParam.getGroupId(), userListParam.getUserIds(),
        userListParam.getNormalMember());
    return Response.success();
  }

  @ResourceAudit
  @ApiOperation(value = "删除用户与组的关联关系")
  @ApiImplicitParams(value = {
      @ApiImplicitParam(name = "userIdGrpIdPairs", value = "组和用户的映射列表", required = true, dataType = "java.util.List", paramType = "query"),
      @ApiImplicitParam(name = "normalMember", value = "普通关联关系(或owner关系)", dataType = "boolean", paramType = "query", defaultValue = "true"),})
  @Override
  public Response<Void> removeUsersFromGroup(UserListParam userListParam) {
    groupService.removeUsersFromGroup(userListParam.getUserIdGroupIdPairs(),
        userListParam.getNormalMember());
    return Response.success();
  }

  @ResourceAudit
  @ApiOperation(value = "移动用户到指定组")
  @ApiImplicitParams(value = {
      @ApiImplicitParam(name = "groupId", value = "目标组id", required = true, dataType = "long", paramType = "query"),
      @ApiImplicitParam(name = "userIdGroupIdPairs", value = "用户和组的原始关系", dataType = "java.util.List", required = true, paramType = "query"),
      @ApiImplicitParam(name = "normalMember", value = "普通关联关系(或owner关系)", dataType = "boolean", paramType = "query", defaultValue = "true")})
  @Override
  public Response<Void> moveGroupUser(UserListParam userListParam) {
    groupService.moveUser(userListParam.getGroupId(), userListParam.getUserIdGroupIdPairs(),
        userListParam.getNormalMember());
    return Response.success();
  }

  @ResourceAudit
  @ApiOperation(value = "向父组中添加子组", notes = "每一个组的code都需要是唯一的")
  @ApiImplicitParams(value = {
      @ApiImplicitParam(name = "tenancyId", value = "租户id(或租户code)", required = true, dataType = "long", paramType = "query"),
      @ApiImplicitParam(name = "targetGroupId", value = "父组id", required = true, dataType = "long", paramType = "query"),
      @ApiImplicitParam(name = "code", value = "子组code", required = true, dataType = "string", paramType = "query"),
      @ApiImplicitParam(name = "name", value = "子组名称", dataType = "string", paramType = "query"),
      @ApiImplicitParam(name = "description", value = "子组描述", dataType = "string", paramType = "query"),
      @ApiImplicitParam(name = "status", value = "子组状态(0,1)", dataType = "integer", paramType = "query", allowableValues = "0,1")})
  @Override
  public Response<GroupDto> addNewGroupIntoGroup(GroupParam groupParam) {
    GroupDto groupDto = groupService.createDescendantGroup(groupParam);
    return Response.success(groupDto);
  }

  @ResourceAudit
  @ApiOperation(value = "根据主键id更新组信息", notes = "每一个组的code都是唯一的")
  @ApiImplicitParams(value = {
      @ApiImplicitParam(name = "id", value = "主键id", required = true, dataType = "long", paramType = "query"),
      @ApiImplicitParam(name = "code", value = "组code(不能为空)", required = true, dataType = "string", paramType = "query"),
      @ApiImplicitParam(name = "name", value = "组名称", dataType = "string", paramType = "query"),
      @ApiImplicitParam(name = "description", value = "组描述", dataType = "string", paramType = "query"),
      @ApiImplicitParam(name = "status", value = "组状态(0,1)", dataType = "integer", paramType = "query", allowableValues = "0,1")})
  @Override
  public Response<GroupDto> updateGroup(GroupParam groupParam) {
    GroupDto groupDto = groupService.updateGroup(groupParam.getId(), groupParam.getCode(),
        groupParam.getName(), groupParam.getStatus(), groupParam.getDescription());
    return Response.success(groupDto);
  }

  @ResourceAudit
  @ApiOperation(value = "根据主键id删除组(与更新组的区别是,该接口会明确将组的所有子组删除)")
  @ApiImplicitParams(value = {
      @ApiImplicitParam(name = "id", value = "主键id", required = true, dataType = "long", paramType = "query")})
  @Override
  public Response<GroupDto> deleteGroup(GroupParam groupParam) {
    return Response.success(groupService.deleteGroup(groupParam.getId()));
  }

  @ApiOperation(value = "根据组id查询所有与其有owner关系的用户列表")
  @ApiImplicitParams(value = {
      @ApiImplicitParam(name = "id", value = "组id", required = true, dataType = "long", paramType = "query")})
  @Override
  @Timed
  public Response<List<UserDto>> getGroupOwners(PrimaryKeyParam primaryKeyParam) {
    return Response.success(groupService.getGroupOwners(primaryKeyParam.getId()));
  }

  @ApiOperation(value = "根据组id查询该组在某个域下的所有角色")
  @ApiImplicitParams(value = {
      @ApiImplicitParam(name = "id", value = "组id", required = true, dataType = "long", paramType = "query"),
      @ApiImplicitParam(name = "domainId", value = "域id", required = true, dataType = "long", paramType = "query")})
  @Override
  public Response<List<RoleDto>> getAllRolesToGroupAndDomain(GroupParam groupParam) {
    return Response.success(
        groupService.getAllRolesToGroupAndDomain(groupParam.getId(), groupParam.getDomainId()));
  }

  @ResourceAudit
  @ApiOperation(value = "关联组和角色")
  @ApiImplicitParams(value = {
      @ApiImplicitParam(name = "id", value = "组id", required = true, dataType = "long", paramType = "query"),
      @ApiImplicitParam(name = "roleIds", value = "角色id列表", required = true, dataType = "java.util.List", paramType = "query")})
  @Override
  public Response<Void> saveRolesToGroup(GroupParam groupParam) {
    groupService.saveRolesToGroup(groupParam.getId(), groupParam.getRoleIds());
    return Response.success();
  }

  @ApiOperation(value = "判断某个用户与某(些)组是否存在owner关系")
  @ApiImplicitParams(value = {
      @ApiImplicitParam(name = "opUserId", value = "用户id", required = true, dataType = "long", paramType = "query"),
      @ApiImplicitParam(name = "targetGroupIds", value = "组id列表", dataType = "java.util.List", paramType = "query"),
      @ApiImplicitParam(name = "targetGroupId", value = "组id(与targetGroupIds至少存在一个)", dataType = "long", paramType = "query")})
  @Override
  public Response<Void> checkOwner(GroupParam groupParam) {
    groupService.checkOwner(groupParam.getOpUserId(), groupParam.getTargetGroupIds(),
        groupParam.getTargetGroupId());
    return Response.success();
  }

  @ResourceAudit
  @ApiOperation(value = "替换组与角色的关联关系")
  @ApiImplicitParams(value = {
      @ApiImplicitParam(name = "id", value = "组id", required = true, dataType = "long", paramType = "query"),
      @ApiImplicitParam(name = "domainId", value = "域id", required = true, dataType = "long", paramType = "query"),
      @ApiImplicitParam(name = "roleIds", value = "组新的角色列表(可为空)", dataType = "java.util.List", paramType = "query")})
  @Override
  public Response<Void> replaceRolesToGroup(GroupParam groupParam) {
    groupService.replaceRolesToGroupUnderDomain(groupParam.getId(), groupParam.getRoleIds(),
        groupParam.getDomainId());
    return Response.success();
  }

  @ApiOperation(value = "查询与组关联的所有的标签信息的列表")
  @ApiImplicitParams(value = {
      @ApiImplicitParam(name = "id", value = "组id", required = true, dataType = "long", paramType = "query"),
      @ApiImplicitParam(name = "domainId", value = "域id", required = true, dataType = "long", paramType = "query")})
  @Override
  public Response<List<TagDto>> queryTagsWithChecked(GroupParam groupParam) {
    return Response
        .success(groupService.searchTagsWithrChecked(groupParam.getId(), groupParam.getDomainId()));
  }

  @ResourceAudit
  @ApiOperation(value = "替换组关联的标签")
  @ApiImplicitParams(value = {
      @ApiImplicitParam(name = "id", value = "组id", required = true, dataType = "long", paramType = "query"),
      @ApiImplicitParam(name = "tagIds", value = "新的标签列表id", required = true, dataType = "java.util.List", paramType = "query")})
  @Override
  public Response<Void> replaceTagsToGrp(GroupParam groupParam) {
    groupService.replaceTagsToGroup(groupParam.getId(), groupParam.getTagIds());
    return Response.success();
  }

  @ResourceAudit
  @ApiOperation(value = "移动组")
  @ApiImplicitParams(value = {
      @ApiImplicitParam(name = "id", value = "目标组id", required = true, dataType = "long", paramType = "query"),
      @ApiImplicitParam(name = "targetGroupId", value = "目标组新的父组id", required = true, dataType = "long", paramType = "query")})
  @Override
  public Response<Void> moveGroup(GroupParam groupParam) {
    groupService.moveGroup(groupParam.getId(), groupParam.getTargetGroupId());
    return Response.success();
  }

  @ApiOperation(value = "判断用户是否与某个组或其子组有关联关系")
  @ApiImplicitParams(value = {
      @ApiImplicitParam(name = "tenancyId", value = "租户id(或租户code)", required = true, dataType = "long", paramType = "query"),
      @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "long", paramType = "query"),
      @ApiImplicitParam(name = "code", value = "组的code", required = true, dataType = "string", paramType = "query"),
      @ApiImplicitParam(name = "includeOwner", value = "目标组新的父组id", dataType = "long", paramType = "query")})
  @Override
  public Response<Boolean> isUserInGroupOrSub(GroupQuery query) {
    Boolean result = groupService
        .isUserInGroupOrSub(query.getUserId(), query.getCode(), query.getIncludeOwner());
    return Response.success(result);
  }

  @ApiOperation(value = "根据用户id获取用户关联的组信息列表")
  @ApiImplicitParams(value = {
      @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "long", paramType = "query"),
      @ApiImplicitParam(name = "includeOwner", value = "关系关系是否包含own关系", dataType = "boolean", paramType = "query", defaultValue = "false"),
      @ApiImplicitParam(name = "includeIndirectAncestors", value = "是否包含用户关联组的父组信息", dataType = "boolean", paramType = "query", defaultValue = "false")})
  @Override
  public Response<List<GroupDto>> listGroupsRelateToUser(GroupQuery query) {
    List<GroupDto> groupList = groupService
        .listGroupsRelateToUser(query.getUserId(), query.getIncludeOwner(),
            query.getIncludeIndirectAncestors());
    return Response.success(groupList);
  }

  @Timed
  @ApiOperation(value = "根据条件查询组的信息(关联的角色,标签,扩展信息,用户等.侧重点在用户的各种信息,而不是根据条件筛选组)")
  @ApiImplicitParams(value = {
      @ApiImplicitParam(name = "tenancyId", value = "租户id(或租户code)", required = true, dataType = "long", paramType = "query"),
      @ApiImplicitParam(name = "id", value = "组id", dataType = "integer", paramType = "query"),
      @ApiImplicitParam(name = "code", value = "组编码(如果有id,则以id为准)", dataType = "string", paramType = "query"),
      @ApiImplicitParam(name = "domainId", value = "域id(查询信息中有域区分的信息,则必传)", dataType = "integer", paramType = "query"),
      @ApiImplicitParam(name = "needGrpRole", value = "查询关联的角色信息", dataType = "boolean", paramType = "query", defaultValue = "false"),
      @ApiImplicitParam(name = "needGrpExtendVal", value = "查询关联的扩展信息", dataType = "boolean", paramType = "query", defaultValue = "false"),
      @ApiImplicitParam(name = "needGrpTag", value = "查询关联的标签信息", dataType = "boolean", paramType = "query", defaultValue = "false"),
      @ApiImplicitParam(name = "needGrpUser", value = "查询关联的用户信息信息", dataType = "boolean", paramType = "query", defaultValue = "false"),
      @ApiImplicitParam(name = "includeDisableUser", value = "关联的用户信息是否包含被禁用用户", dataType = "boolean", paramType = "query", defaultValue = "false"),
      @ApiImplicitParam(name = "needGrpUserRole", value = "关联用户的角色信息", dataType = "boolean", paramType = "query", defaultValue = "false"),
      @ApiImplicitParam(name = "needGrpUserTag", value = "关联用户的标签信息", dataType = "boolean", paramType = "query", defaultValue = "false"),
      @ApiImplicitParam(name = "needGrpUserExtendVal", value = "关联用户的扩展属性信息", dataType = "boolean", paramType = "query", defaultValue = "false")})
  @Override
  public Response<PageDto<GroupDto>> queryTotalInfo(GroupParam groupParam) {
    return Response.success(groupService.queryTotalInfo(groupParam.getId(), groupParam.getCode(),
        groupParam.getDomainId(), groupParam.getNeedGrpRole(), groupParam.getNeedGrpExtendVal(),
        groupParam.getNeedGrpTag(), groupParam.getNeedGrpUser(), groupParam.getIncludeDisableUser(),
        groupParam.getNeedGrpUserRole(), groupParam.getNeedGrpUserTag(),
        groupParam.getNeedGrpUserExtendVal(), groupParam.getPageNumber(),
        groupParam.getPageSize()));
  }

  @ApiOperation(value = "根据用户id列表查询用户关联的组信息(如果查询单个用户.可使用listGroupsRelateToUser,提供更丰富的条件选项.)")
  @ApiImplicitParams(value = {
      @ApiImplicitParam(name = "tenancyId", value = "租户id(或租户code)", required = true, dataType = "long", paramType = "query"),
      @ApiImplicitParam(name = "userIds", value = "用户id集合", required = true, dataType = "java.util.List", paramType = "query"),
      @ApiImplicitParam(name = "rootGrpCode", value = "限定组查询的范围(只查询rootGrpCode的子组)", dataType = "java.lang.String", paramType = "query")})
  @Override
  public Response<List<UserGroupDto>> queryGrpListRelateUser(UserGroupParam userGroupParam) {
    return Response.success(groupService
        .queryGrpListRelateUser(userGroupParam.getUserIds(), userGroupParam.getRootGrpCode()));
  }
}
