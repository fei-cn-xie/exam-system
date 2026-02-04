import org.apache.commons.lang3.StringUtils;

public class Test {
    public static void main(String[] args) {
        String policy = """
                {
                    "Version": "2012-10-17",
                    "Statement": [
                        {
                            "Effect": "Allow",
                            "Principal": {
                                "AWS": [
                                    "*"
                                ]
                            },
                            "Action": [
                                "s3:GetObject"
                            ],
                            "Resource": [
                                "arn:aws:s3:::%s/*"
                            ]
                        }
                    ]
                }
        """;
        String bucket = policy.formatted("vucket");
        System.out.println(bucket);
        System.out.println(StringUtils.isEmpty(null));
    }
}
