package org.example.ecommerce.domain.model.admin;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.ecommerce.domain.common.Address;
import org.example.ecommerce.domain.common.BaseEntity;
import org.example.ecommerce.domain.model.user.Authority;
import org.example.ecommerce.domain.model.user.User;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "admins")
public class Admin extends User {


}
