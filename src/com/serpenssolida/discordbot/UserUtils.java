package com.serpenssolida.discordbot;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.ArrayList;
import java.util.HashSet;

public class UserUtils
{
	/**
	 * Search multiple users by the given name.
	 *
	 * @param guild
	 * 		The guild from where to search from.
	 * @param userName
	 * 		The username of the users to search for.
	 *
	 * @return
	 * 		An ArrayList that contains all the users that match the query.
	 */
	public static ArrayList<Member> findUsersByName(Guild guild, String userName)
	{
		HashSet<Member> users = new HashSet<>();
		
		users.addAll(guild.getMembersByName(userName, true));
		users.addAll(guild.getMembersByNickname(userName, true));
		
		return new ArrayList<>(users);
	}
	
	/**
	 * Check if the user has administration permission.
	 *
	 * @param member
	 * 		The user to check.
	 *
	 * @return
	 * 		-True if the user has administration permission.
	 * 		-False if the user has not administration permission.
	 */
	public static boolean hasMemberAdminPermissions(Member member)
	{
		if (member == null)
			return false;
		
		return member.isOwner() || UserUtils.canMemberManageServer(member);
	}
	
	/**
	 * Check if the user has MANAGE_SERVER permission.
	 *
	 * @param member
	 * 		The user to check.
	 *
	 * @return
	 * 		-True if the user has administration permission.
	 * 		-False if the user has not administration permission.
	 */
	public static boolean canMemberManageServer(Member member)
	{
		for (Role role : member.getRoles())
		{
			if (role.hasPermission(Permission.MANAGE_SERVER))
				return true;
		}
		
		return false;
	}
}
