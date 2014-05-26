package pl.ciruk.blog.jpa_unit_tests;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(schema="blogdb", name="SAMPLE_TABLE")
@NamedQueries({
	@NamedQuery(name=SampleEntity.Query.WITH_AMOUNT_BELOW, query="select se from SampleEntity se where amount < :amountThreshold")
})
public class SampleEntity {
	
	public class Query {
		/** Retrieve all SampleEntities with amount below given value. Takes one named parameter: {@code amountThreshold}. */
		public static final String WITH_AMOUNT_BELOW = "SampleEntity.WithAmountBelow";
	}
	
	@Id
	@Column(name="ID", nullable=false)
	private Long id;
	
	@Column(name="NAME", nullable=false, length=50)
	private String name;
	
	@Column(name="ALT_TEXT", nullable=true, length=100)
	private String label;
	
	@Column(name="AMNT", nullable=false, precision=20)
	private Double amount;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof SampleEntity)) {
			return false;
		}
		SampleEntity other = (SampleEntity) obj;
		if (amount == null) {
			if (other.amount != null) {
				return false;
			}
		} else if (!amount.equals(other.amount)) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (label == null) {
			if (other.label != null) {
				return false;
			}
		} else if (!label.equals(other.label)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
