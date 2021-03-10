package comm.hummer.simple.common.module;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

@Data
public class DifferQueryStringDto implements Serializable {
    private static final long serialVersionUID = 7152083639351568260L;

    @NotEmpty(message = "uuId can't null")
    private String uuId;
    private Collection<String> users;
    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date atTime;
    private String atString;
}
