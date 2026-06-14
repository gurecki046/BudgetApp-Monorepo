package pk.fg.pasir_gorka_filip.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;
import pk.fg.pasir_gorka_filip.model.User;

@Entity
@Getter
@Setter
@NoArgsConstructor

@Table(name = "`groups`")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Group name

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner; // Group owner (can invite and remove other users)

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Membership> memberships; // List of group memberships (users in the group)

    @Transient
    public Long getOwnerId() {
        return owner != null ? owner.getId() : null;
    }
}
