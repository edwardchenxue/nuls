/**
 * MIT License
 *
 * Copyright (c) 2017-2018 nuls.io
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.nuls.core.module.thread;

import io.nuls.core.constant.ErrorCode;
import io.nuls.core.constant.ModuleStatusEnum;
import io.nuls.core.constant.NulsConstant;
import io.nuls.core.exception.NulsRuntimeException;
import io.nuls.core.module.BaseModuleBootstrap;
import io.nuls.core.module.manager.ModuleManager;
import io.nuls.core.utils.log.Log;
import io.nuls.core.utils.str.StringUtils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Niels
 * @date 2017/11/24
 */
public class ModuleRunner implements Runnable {

    private final String moduleKey;
    private final String moduleClass;
    private BaseModuleBootstrap module;

    public ModuleRunner(String key, String moduleClass) {
        this.moduleKey = key;
        this.moduleClass = moduleClass;
    }

    @Override
    public void run() {
        try {
            module = this.loadModule();
            module.setStatus(ModuleStatusEnum.INITIALIZING);
            module.init();
            module.setStatus(ModuleStatusEnum.INITIALIZED);
            module.setStatus(ModuleStatusEnum.STARTING);
            module.start();
            module.setStatus(ModuleStatusEnum.RUNNING);
        } catch (ClassNotFoundException e) {
            module.setStatus(ModuleStatusEnum.EXCEPTION);
            Log.error(e);
            throw new NulsRuntimeException(ErrorCode.FAILED, e);
        } catch (IllegalAccessException e) {
            module.setStatus(ModuleStatusEnum.EXCEPTION);
            Log.error(e);
            throw new NulsRuntimeException(ErrorCode.FAILED, e);
        } catch (InstantiationException e) {
            module.setStatus(ModuleStatusEnum.EXCEPTION);
            Log.error(e);
            throw new NulsRuntimeException(ErrorCode.FAILED, e);
        }
    }

    private BaseModuleBootstrap loadModule() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        BaseModuleBootstrap module = null;
        do {
            if (StringUtils.isBlank(moduleClass)) {
                Log.warn("module cannot start:" + moduleClass);
                break;
            }
            Class clazz = Class.forName(moduleClass);
            module = (BaseModuleBootstrap) clazz.newInstance();
            module.setModuleName(this.moduleKey);
            Log.info("load module:" + module.getInfo());
        } while (false);
        ModuleManager.getInstance().regModule(module);

        return module;
    }

    public String getModuleKey() {
        return moduleKey;
    }

    public BaseModuleBootstrap getModule() {
        return module;
    }
}
