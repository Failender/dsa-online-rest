package de.failender.dsaonline.service;

import de.failender.dsaonline.data.repository.*;
import de.failender.dsaonline.rest.dto.StatisticEntry;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdministrationService {


	private final UserRepository userRepository;
	private final AbenteuerRepository abenteuerRepository;
	private final HeldRepository heldRepository;
	private final VersionRepository versionRepository;
	private final GruppeRepository gruppeRepository;


	public AdministrationService(UserRepository userRepository, AbenteuerRepository abenteuerRepository, HeldRepository heldRepository, VersionRepository versionRepository, GruppeRepository gruppeRepository) {
		this.userRepository = userRepository;
		this.abenteuerRepository = abenteuerRepository;
		this.heldRepository = heldRepository;
		this.versionRepository = versionRepository;
		this.gruppeRepository = gruppeRepository;
	}

	public List<StatisticEntry> getStatistics() {
		List<StatisticEntry> entries = new ArrayList<>();
		entries.add(new StatisticEntry("Nutzer", userRepository.count()));
		entries.add(new StatisticEntry("Abenteuer", abenteuerRepository.count()));
		entries.add(new StatisticEntry("Helden", heldRepository.count()));
		entries.add(new StatisticEntry("Held-Versionen", versionRepository.count()));
		entries.add(new StatisticEntry("Gruppen", gruppeRepository.count()));

		return entries;
	}


}
