package com.Iplus.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@IdClass(tb_user_like_id.class)
public class tb_user_like{
	
	@Id
	@ManyToOne
	@JoinColumn(referencedColumnName = "uid")
	@JsonBackReference
	private tb_member likeUid;
	
	@Id
	@ManyToOne
	@JoinColumn(referencedColumnName = "acNum")
	@JsonBackReference
	private tb_admin_scalp_care likeAcNum;

	
	@Column
	private Boolean good;
	
	@Override
	public String toString() {
		return "tb_user_like";
	}
	

}
