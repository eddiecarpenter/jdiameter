package io.go.diameter.runtime.config;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

import java.util.List;
import java.util.Optional;

@ConfigGroup
public interface LocalPeer
{
	/**
	 * Specifies the URI for the local peer. The URI has the following format: "aaa://FQDN:port".
	 */
	@WithDefault("aaa://localhost:1812")
	@WithName("uri")
	String uri();

	/**
	 * Contains one or more valid IP address for the local peer.`
	 */
	@WithName("ip-addresses")
	@WithDefault("127.0.0.1")
	List<String> ipAddresses();

	/**
	 * Specifies the realm of the local peer.
	 */
	@WithName("realm")
	@WithDefault("io.go.diameter")
	String realm();

	/**
	 * Specifies the name of the local peer product
	 */
	@WithDefault("Go Diameter")
	@WithName("product-name")
	String productName();

	/**
	 * Specifies the version of the firmware.
	 */
	@WithDefault("3")
	@WithName("firmware-revision")
	long firmwareRevision();

	/**
	 * Specifies a numeric identifier that corresponds to the vendor ID allocated by IANA.
	 */
	@WithDefault("0")
	@WithName("vendor-id")
	long vendorId();

	/**
	 * Contains a list of default supported applications.
	 */
	@WithName("applications")
	Optional<List<ApplicationId>> applications();

	/**
	 * Optional parent element containing child elements that specify settings
	 * relating to the Overload Monitor.
	 */
	@WithName("overload-monitors")
	Optional<List<OverloadMonitorEntry>> overloadMonitors();
}