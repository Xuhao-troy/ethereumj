/*
 * Copyright (c) [2016] [ <ether.camp> ]
 * This file is part of the ethereumJ library.
 *
 * The ethereumJ library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ethereumJ library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ethereumJ library. If not, see <http://www.gnu.org/licenses/>.
 */
package org.ethereum.vm.hook;

import org.ethereum.vm.OpCode;
import org.ethereum.vm.program.Program;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Primary {@link VMHook} implementation, that accepts other VM hook components to safely proxy all invocations to them.
 */
@Primary
@Component
public class RootVmHook implements VMHook {

    private static final Logger logger = LoggerFactory.getLogger("VM");

    private final List<VMHook> hooks;

    @Autowired
    public RootVmHook(Optional<List<VMHook>> hooks) {
        this.hooks = hooks.orElseGet(ArrayList::new);
    }

    private void safeProxyToAll(Consumer<VMHook> action) {
        this.hooks.forEach(hook -> {
            try {
                action.accept(hook);
            } catch (Throwable t) {
                logger.error("VM hook execution error: ", t);
            }
        });
    }

    @Override
    public void startPlay(Program program) {
        safeProxyToAll(hook -> hook.startPlay(program));
    }

    @Override
    public void step(Program program, OpCode opcode) {
        safeProxyToAll(hook -> hook.startPlay(program));
    }

    @Override
    public void stopPlay(Program program) {
        safeProxyToAll(hook -> hook.startPlay(program));
    }
}
