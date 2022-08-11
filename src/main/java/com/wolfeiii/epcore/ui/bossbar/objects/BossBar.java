package com.wolfeiii.epcore.ui.bossbar.objects;

import com.wolfeiii.epcore.utilities.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.N;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.stream.Collectors;

public class BossBar {

    private final org.bukkit.boss.BossBar bossBar;

    public BossBar(@NotNull org.bukkit.boss.BossBar bukkitBossBar) {
        this.bossBar = bukkitBossBar;
    }

    public String title() {
        return bossBar.getTitle();
    }

    public BossBar title(@NotNull String title) {
        bossBar.setTitle(title);
        return this;
    }

    public List<Player> players() {
        return bossBar.getPlayers();
    }

    public BossBar players(@NotNull List<Player> players) {
        players.forEach(this.bossBar::addPlayer);
        return this;
    }

    public float progress() {
        return (float) bossBar.getProgress();
    }

    public BossBar progress(float progress){
        bossBar.setProgress(progress);
        return this;
    }

    public @NotNull @Unmodifiable List<BossBarFlag> flags() {
        return Arrays.stream(BossBarFlag.values())
                .filter(bossBarFlag -> {
                    try {
                        return bossBar.hasFlag(BarFlag.valueOf(bossBarFlag.name().replace("CREATE_WORLD_FOG", "CREATE_FOG")));
                    } catch (Throwable ignored) {
                        return false;
                    }
                }).toList();
    }

    public BossBar flags(@NotNull List<BossBarFlag> flags) {
        for (BarFlag barFlag : BarFlag.values()) {
            try {
                String valueName = barFlag.name().replace("CREATE_FOG", "CREATE_WORLD_FOG");
                Optional<BossBarFlag> flag = flags.stream().filter(bossBarFlag -> bossBarFlag.name().equals(valueName)).findFirst();

                if (bossBar.hasFlag(barFlag)) {
                    if (flag.isEmpty()) {
                        bossBar.removeFlag(barFlag);
                    }
                } else if (flag.isPresent()) {
                    bossBar.addFlag(barFlag);
                }
            } catch (Throwable ignored) {
                if (bossBar.hasFlag(barFlag)) {
                    bossBar.removeFlag(barFlag);
                }
            }
        }

        return this;
    }

    public BossBarColor color() {
        try {
            return BossBarColor.valueOf(bossBar.getColor().name());
        } catch (Throwable ignored) {
            return BossBarColor.GREEN;
        }
    }

    public BossBar color(@NotNull BossBarColor color) {
        try {
            bossBar.setColor(BarColor.valueOf(color.name()));
        } catch (Throwable ignored) {
            // Ignore exception
        }

        return this;
    }

    public BossBarDivision division() {
        try {
            String style = bossBar.getStyle().name().replace("SEGMENTED", "NOTCHED").replace("SOLID", "NO_DIVISION");
            return BossBarDivision.valueOf(style);
        } catch (Throwable ignored) {
            return BossBarDivision.NO_DIVISION;
        }
    }
    public BossBar division(@NotNull BossBarDivision division) {
        try {
            String style = division.name().replace("NOTCHED", "SEGMENTED").replace("NO_DIVISION", "SOLID");
            bossBar.setStyle(BarStyle.valueOf(style));
        } catch (Throwable ignored) {
        }

        return this;
    }

    public BossBar remove() {
        this.bossBar.removeAll();
        return this;
    }

    public static class BossBarBuilder {
        private String title = "";
        private float progress = 0f;
        private BossBarColor color = BossBarColor.GREEN;
        private BossBarDivision division = BossBarDivision.NO_DIVISION;
        private Collection<BossBarFlag> flags;
        private Collection<Player> players;

        @NotNull
        public BossBarBuilder title(@NotNull String title) {
            this.title = Utilities.translate(title);
            return this;
        }

        @NotNull
        public BossBarBuilder progress(float progress) {
            if (progress > 1) {
                progress = progress / 100;
            }

            this.progress = progress;
            return this;
        }

        @NotNull
        public BossBarBuilder color(@NotNull BossBarColor color) {
            this.color = color;
            return this;
        }

        @NotNull
        public BossBarBuilder division(@NotNull BossBarDivision division) {
            this.division = division;
            return this;
        }

        @NotNull
        public BossBarBuilder flags(@NotNull Collection<BossBarFlag> flags) {
            this.flags = flags;
            return this;
        }

        @NotNull
        public BossBarBuilder flags(@NotNull BossBarFlag... flags) {
            this.flags = Arrays.asList(flags);
            return this;
        }

        @NotNull
        public BossBarBuilder players(@NotNull Player... players) {
            this.players = Arrays.asList(players);
            return this;
        }

        @NotNull
        public BossBar build() {
            BossBar bossBar = new BossBar(Bukkit.createBossBar(
                    title,
                    BarColor.PURPLE,
                    BarStyle.SOLID
            ));

            bossBar.color(color);
            bossBar.division(division);
            bossBar.progress(progress);
            if (flags != null && !flags.isEmpty()) {
                bossBar.flags(List.copyOf(flags));
            }

            if (players != null && !players.isEmpty()) {
                bossBar.players(List.copyOf(players));
            }

            return bossBar;
        }
    }

}
