// This contains functions taht are common to both the all.js filter and the post-process.js hook.
const _ = require('lodash');

class ScsLib {
  // This returns a valid Java class name.
  getClassName(name) {
    const ret = this.getIdentifierName(name);
    return _.upperFirst(ret);
  }

  // This returns a valid Java identifier name.
  getIdentifierName(name) {
    let ret = _.camelCase(name);

    if (ScsLib.reservedWords.has(ret)) {
      ret = `_${  ret}`;
    }

    return ret;
  }

  // This returns the value of a param, or specification extention if the param isn't set. 
  // If neither is set and the required flag is true, it throws an error.
  getParamOrExtension(info, params, paramName, extensionName, description, example, required) {
    let ret = '';
    if (params[paramName]) {
      ret = params[paramName];
    } else if (info.extensions()[extensionName]) {
      ret = info.extensions()[extensionName];
    } else if (required) {
      throw new Error(`Can't determine the ${description}. Please set the param ${paramName} or info.${extensionName}. Example: ${example}`);
    }
    return ret;
  }

  // This returns the value of a param, or specification extention if the param isn't set. 
  // If neither is set it returns defaultValue.
	getParamOrDefault(info, params, paramName, extensionName, defaultValue) {
		let ret = '';
		if (params[paramName]) {
			ret = params[paramName];
		} else if (info.extensions()[extensionName]) {
			ret = info.extensions()[extensionName];
		} else  {
			ret = defaultValue;
		}
		return ret;
	}

	/*
	By default, the 'view' is 'client', which means that when the doc says subscribe, we publish.
	By setting the view to 'provider', when the doc says subscribe, we subscribe.
	*/
	isProvidererView(info, params) {
		let view = this.getParamOrDefault(info, params, 'view', 'x-view', 'client');
		return view === 'provider'
  }
  
  /*
  See isProviderView above.
  This returns true if the operation should physically subscribe, based on the 'view' param.
  */
  isRealSubscriber(info, params, operation) {
    let isProvider = this.isProvidererView(info, params);
    let ret = (isProvider && operation.isSubscribe()) || (!isProvider && !operation.isSubscribe());
    console.log(`isRealSubscriber: isProvider: ${isProviderer} isSubscribe: ${operation.isSubscribe()}`);
    return ret;
  }

  getRealPublisher(info, params, channel) {
    let isProvider = this.isProvidererView(info, params);
    return isProvider ? channel.publish() : channel.subscribe();
  }

  getRealSubscriber(info, params, channel) {
    let isProvider = this.isProvidererView(info, params);
    return isProvider ? channel.subscribe() : channel.publish();
  }
}

// This is the set of Java reserved words, to ensure that we don't generate reserved words.
ScsLib.reservedWords = new Set();
ScsLib.reservedWords.add('abstract');
ScsLib.reservedWords.add('assert');
ScsLib.reservedWords.add('boolean');
ScsLib.reservedWords.add('break');
ScsLib.reservedWords.add('boolean');
ScsLib.reservedWords.add('byte');
ScsLib.reservedWords.add('case');
ScsLib.reservedWords.add('catch');
ScsLib.reservedWords.add('char');
ScsLib.reservedWords.add('class');
ScsLib.reservedWords.add('const');
ScsLib.reservedWords.add('continue');
ScsLib.reservedWords.add('default');
ScsLib.reservedWords.add('do');
ScsLib.reservedWords.add('double');
ScsLib.reservedWords.add('else');
ScsLib.reservedWords.add('enum');
ScsLib.reservedWords.add('extends');
ScsLib.reservedWords.add('final');
ScsLib.reservedWords.add('finally');
ScsLib.reservedWords.add('float');
ScsLib.reservedWords.add('for');
ScsLib.reservedWords.add('if');
ScsLib.reservedWords.add('goto');
ScsLib.reservedWords.add('implements');
ScsLib.reservedWords.add('import');
ScsLib.reservedWords.add('instalceof');
ScsLib.reservedWords.add('int');
ScsLib.reservedWords.add('interface');
ScsLib.reservedWords.add('long');
ScsLib.reservedWords.add('native');
ScsLib.reservedWords.add('new');
ScsLib.reservedWords.add('package');
ScsLib.reservedWords.add('private');
ScsLib.reservedWords.add('proteccted');
ScsLib.reservedWords.add('public');
ScsLib.reservedWords.add('return');
ScsLib.reservedWords.add('short');
ScsLib.reservedWords.add('static');
ScsLib.reservedWords.add('strictfp');
ScsLib.reservedWords.add('super');
ScsLib.reservedWords.add('switch');
ScsLib.reservedWords.add('syncronized');
ScsLib.reservedWords.add('this');
ScsLib.reservedWords.add('throw');
ScsLib.reservedWords.add('throws');
ScsLib.reservedWords.add('transient');
ScsLib.reservedWords.add('try');
ScsLib.reservedWords.add('void');
ScsLib.reservedWords.add('volatile');
ScsLib.reservedWords.add('while');

module.exports = ScsLib;
