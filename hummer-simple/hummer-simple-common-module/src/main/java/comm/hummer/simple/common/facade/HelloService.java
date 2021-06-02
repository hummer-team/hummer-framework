package comm.hummer.simple.common.facade;

import comm.hummer.simple.common.module.SimpleDubboDto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Map;

public interface HelloService {
    String save(@Size(min = 1, message = "min size 1") Map<String, Object> demo);

    Integer add(@NotNull(message = "can't null") @Min(message = "min value is 1", value = 1) Integer a,
                @NotNull(message = "can't null") @Min(message = "min value is 1", value = 1) Integer b);

    Integer add2(@NotNull(message = "dto not null") SimpleDubboDto dto);
}
